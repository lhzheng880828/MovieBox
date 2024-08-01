package com.calvin.box.movie.nano.process

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import com.calvin.box.movie.AndroidPlayers
import com.calvin.box.movie.nano.Nano
import com.calvin.box.movie.nano.Server
import com.calvin.box.movie.player.Players
import com.google.gson.JsonObject
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.IHTTPSession
import java.util.Objects

class Media : Process {
    override fun isRequest(session: IHTTPSession, path: String): Boolean {
        return "/media" == path
    }

    override fun doResponse(
        session: IHTTPSession,
        path: String,
        files: Map<String, String>
    ): NanoHTTPD.Response {
        if (isNull) return Nano.success("{}")
        val result = JsonObject()
        result.addProperty("url", url)
        result.addProperty("state", state)
        result.addProperty("speed", speed)
        result.addProperty("title", title)
        result.addProperty("artist", artist)
        result.addProperty("artwork", artUri)
        result.addProperty("duration", duration)
        result.addProperty("position", position)
        return Nano.success(result.toString())
    }

    private val player: AndroidPlayers?
        get() = Server.get().player

    private val isNull: Boolean
        get() = Objects.isNull(player) || Objects.isNull(player?.getSession())

    private val playbackState: PlaybackStateCompat?
        get() = player?.getSession()?.controller?.playbackState

    private val metadata: MediaMetadataCompat?
        get() = player?.getSession()?.controller?.metadata

    private val url: String?
        get() = if (TextUtils.isEmpty(player?.getUrl())) "" else player?.getUrl()

    private val title: String
        get() = if (metadata == null || metadata!!.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                .isEmpty()
        ) "" else metadata!!.getString(MediaMetadataCompat.METADATA_KEY_TITLE)

    private val artist: String
        get() = if (metadata == null || metadata!!.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                .isEmpty()
        ) "" else metadata!!.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

    private val artUri: String
        get() = if (metadata == null) "" else metadata!!.getString(MediaMetadataCompat.METADATA_KEY_ART_URI)

    private val duration: Long
        get() = if (metadata == null) -1 else metadata!!.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)

    private val state: Int
        get() = if (playbackState == null) -1 else playbackState!!.state

    private val position: Long
        get() = if (playbackState == null) -1 else playbackState!!.position

    private val speed: Float
        get() = if (playbackState == null) -1.0f else playbackState!!.playbackSpeed
}