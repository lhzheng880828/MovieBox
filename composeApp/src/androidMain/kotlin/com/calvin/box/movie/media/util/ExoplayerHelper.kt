package com.calvin.box.movie.media.util

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.calvin.box.movie.player.exo.ExoUtil
import io.github.aakira.napier.Napier

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun rememberPlayerView(exoPlayer: ExoPlayer, context: Context): PlayerView {
    val playerView = remember(context) {
        PlayerView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            player = exoPlayer
            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            playerView.player = null
        }
    }
    return playerView
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun rememberExoPlayerWithLifecycle(
    reelUrl: String,
    context: Context,
    isPause: Boolean
): ExoPlayer {
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context)
            .setLoadControl(ExoUtil.buildLoadControl())
            .setTrackSelector(ExoUtil.buildTrackSelector())
            .setRenderersFactory(ExoUtil.buildRenderersFactory())
            .setMediaSourceFactory(ExoUtil.buildMediaSourceFactory())
            .setTrackSelector(DefaultTrackSelector(context))
            .build()
            .apply {
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                repeatMode = Player.REPEAT_MODE_ONE
                setHandleAudioBecomingNoisy(true)
            }
    }

    LaunchedEffect(reelUrl) {
        val videoUri = Uri.parse(reelUrl)
        var mediaItem = MediaItem.fromUri(videoUri)
        val mediaInfo = getPlayMediaInfo()
        if(mediaInfo!=null){
            val headerMap = mediaInfo.headers
            mediaItem = ExoUtil.getMediaItem(headerMap, videoUri, mediaInfo.mimeType, mediaInfo.drm, mediaInfo.subs )
            Napier.d { "Exoplayer url: $videoUri, headers: $headerMap" }
        } else {
            Napier.d { "Exoplayer url: $videoUri, mediaInfo is null" }
        }
        /*val dataSourceFactory = DefaultDataSource.Factory(context)
        val mediaSource = when {
            reelUrl.endsWith(".m3u8", ignoreCase = true) -> {
                // HLS media source
                HlsMediaSource.Factory(DefaultHttpDataSource.Factory())
                    .createMediaSource(mediaItem)
            }
            reelUrl.endsWith(".mpd", ignoreCase = true) ||
                    reelUrl.endsWith("type=mpd", ignoreCase = true) -> {
                // DASH media source
                DashMediaSource.Factory(DefaultHttpDataSource.Factory())
                    .createMediaSource(mediaItem)
            }
            else -> {
                // Progressive or other formats (mp4, mkv, etc.)
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem)
            }
        }*/
        exoPlayer.seekTo(0, 0)
        //exoPlayer.setMediaSource(mediaSource)
        exoPlayer.setMediaItem(mediaItem, 0)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    var appInBackground by remember {
        mutableStateOf(false)
    }

    DisposableEffect(key1 = lifecycleOwner, appInBackground) {
        val lifecycleObserver =
            getExoPlayerLifecycleObserver(exoPlayer, isPause, appInBackground) {
                appInBackground = it
            }
        lifecycleOwner.value.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.value.lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return exoPlayer
}

