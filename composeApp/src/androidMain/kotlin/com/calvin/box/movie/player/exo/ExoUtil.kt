package com.calvin.box.movie.player.exo

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.accessibility.CaptioningManager
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.DrmConfiguration
import androidx.media3.common.MediaItem.SubtitleConfiguration
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.TrackSelector
import androidx.media3.ui.CaptionStyleCompat
import com.calvin.box.movie.ContextProvider
import com.calvin.box.movie.Setting
import com.calvin.box.movie.bean.Drm
import com.calvin.box.movie.bean.Sub
import com.calvin.box.movie.player.Players
import java.util.Locale
import java.util.UUID

object ExoUtil {
    @OptIn(markerClass = [UnstableApi::class])
    fun buildLoadControl(): LoadControl {
        return DefaultLoadControl()
    }

    @OptIn(markerClass = [UnstableApi::class])
    fun buildTrackSelector(): TrackSelector {
        val context = ContextProvider.context as Context
        val trackSelector: DefaultTrackSelector = DefaultTrackSelector(context)
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setPreferredTextLanguage(
                Locale.getDefault().isO3Language
            ).setForceHighestSupportedBitrate(true).setTunnelingEnabled(Setting.isTunnel)
        )
        return trackSelector
    }

    @OptIn(markerClass = [UnstableApi::class])
    fun buildRenderersFactory(decode: Int = Players.HARD): RenderersFactory {
        return NextRenderersFactory((ContextProvider.context as Context), decode)
    }

    @OptIn(UnstableApi::class)
    fun buildMediaSourceFactory(): MediaSource.Factory {
        return MediaSourceFactory()
    }

    @get:OptIn(markerClass = [UnstableApi::class])
    val captionStyle: CaptionStyleCompat
        get() = if (Setting.isCaption) CaptionStyleCompat.createFromCaptionStyle(
            ((ContextProvider.context as Context).getSystemService(
                Context.CAPTIONING_SERVICE
            ) as CaptioningManager).getUserStyle()
        ) else CaptionStyleCompat(
            Color.WHITE,
            Color.TRANSPARENT,
            Color.TRANSPARENT,
            CaptionStyleCompat.EDGE_TYPE_OUTLINE,
            Color.BLACK,
            null
        )

    fun haveTrack(tracks: Tracks, type: Int): Boolean {
        var count = 0
        for (trackGroup in tracks.getGroups()) if (trackGroup.getType() == type) count += trackGroup.length
        return count > 0
    }

    fun selectTrack(player: ExoPlayer, group: Int, track: Int) {
        val trackIndices: MutableList<Int> = ArrayList()
        selectTrack(player, group, track, trackIndices)
        setTrackParameters(player, group, trackIndices)
    }

    fun deselectTrack(player: ExoPlayer, group: Int, track: Int) {
        val trackIndices: MutableList<Int> = ArrayList()
        deselectTrack(player, group, track, trackIndices)
        setTrackParameters(player, group, trackIndices)
    }

    fun getMimeType(path: String): String {
        if (TextUtils.isEmpty(path)) return ""
        if (path.endsWith(".vtt")) return MimeTypes.TEXT_VTT
        if (path.endsWith(".ssa") || path.endsWith(".ass")) return MimeTypes.TEXT_SSA
        if (path.endsWith(".ttml") || path.endsWith(".xml") || path.endsWith(".dfxp")) return MimeTypes.APPLICATION_TTML
        return MimeTypes.APPLICATION_SUBRIP
    }

    fun getMimeType(errorCode: Int): String? {
        if (errorCode == PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED || errorCode == PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED || errorCode == PlaybackException.ERROR_CODE_IO_UNSPECIFIED) return MimeTypes.APPLICATION_M3U8
        return null
    }

    fun getRetry(errorCode: Int): Int {
        if (errorCode == PlaybackException.ERROR_CODE_IO_UNSPECIFIED) return 2
        if (errorCode == PlaybackException.ERROR_CODE_DECODER_INIT_FAILED) return 2
        if (errorCode >= PlaybackException.ERROR_CODE_DECODER_QUERY_FAILED && errorCode <= PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED) return 2
        if (errorCode >= PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED && errorCode <= PlaybackException.ERROR_CODE_PARSING_MANIFEST_UNSUPPORTED) return 2
        return 1
    }

    fun getMediaItem(
        headers: Map<String, String>,
        uri: Uri,
        mimeType: String?,
        drm: Drm?,
        subs: List<Sub>,
        decode: Int
    ): MediaItem {
        val builder = MediaItem.Builder().setUri(uri)
        builder.setRequestMetadata(getRequestMetadata(headers, uri))
        builder.setSubtitleConfigurations(getSubtitleConfigs(subs))
        if (drm != null) builder.setDrmConfiguration(getDrmConfig(drm))
        if (mimeType != null) builder.setMimeType(mimeType)
        builder.setMediaId(uri.toString())
        return builder.build()
    }

    fun getDrmConfig(drm:Drm): DrmConfiguration {
        return DrmConfiguration.Builder(getUUID(drm)).setLicenseUri(drm.key).build()
    }

    private fun getUUID(drm: Drm): UUID {
        if (drm.type.contains("playready")) return C.PLAYREADY_UUID
        if (drm.type.contains("widevine")) return C.WIDEVINE_UUID
        if (drm.type.contains("clearkey")) return C.CLEARKEY_UUID
        return C.UUID_NIL
    }

    private fun getRequestMetadata(
        headers: Map<String, String>,
        uri: Uri
    ): MediaItem.RequestMetadata {
        val extras = Bundle()
        for ((key, value) in headers) extras.putString(key, value)
        return MediaItem.RequestMetadata.Builder().setMediaUri(uri).setExtras(extras).build()
    }

    private fun getSubtitleConfigs(subs: List<Sub>): List<MediaItem.SubtitleConfiguration> {
        val configs: MutableList<MediaItem.SubtitleConfiguration> = ArrayList()
        for (sub in subs) configs.add(getConfig(sub))
        return configs
    }

    fun getConfig(sub: Sub): SubtitleConfiguration {
        return SubtitleConfiguration.Builder(Uri.parse(sub.url)).setLabel(sub.name)
            .setMimeType(sub.format).setSelectionFlags(sub.flag).setLanguage(sub.lang).build()
    }

    private fun selectTrack(
        player: ExoPlayer,
        group: Int,
        track: Int,
        trackIndices: MutableList<Int>
    ) {
        if (group >= player.getCurrentTracks().getGroups().size) return
        val trackGroup: Tracks.Group = player.getCurrentTracks().getGroups().get(group)
        for (i in 0 until trackGroup.length) {
            if (i == track || trackGroup.isTrackSelected(i)) trackIndices.add(i)
        }
    }

    private fun deselectTrack(
        player: ExoPlayer,
        group: Int,
        track: Int,
        trackIndices: MutableList<Int>
    ) {
        if (group >= player.getCurrentTracks().getGroups().size) return
        val trackGroup: Tracks.Group = player.getCurrentTracks().getGroups().get(group)
        for (i in 0 until trackGroup.length) {
            if (i != track && trackGroup.isTrackSelected(i)) trackIndices.add(i)
        }
    }

    private fun setTrackParameters(player: ExoPlayer, group: Int, trackIndices: List<Int>) {
        if (group >= player.getCurrentTracks().getGroups().size) return
        player.setTrackSelectionParameters(
            player.getTrackSelectionParameters().buildUpon().setOverrideForType(
                TrackSelectionOverride(
                    player.getCurrentTracks().getGroups().get(group).getMediaTrackGroup(),
                    trackIndices
                )
            ).build()
        )
    }
}
