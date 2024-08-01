package com.calvin.box.movie

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media3.common.C
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.impl.SessionCallback
import kotlinx.coroutines.runBlocking
import java.lang.Runnable
import java.util.Formatter
import java.util.Locale

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/8/1
 */
class AndroidPlayers {
    private var session: MediaSessionCompat? = null
    private val url: String = ""

    private var decode = 0
    private val count = 0
    private var player = 0
    private lateinit var builder:  StringBuilder
    private lateinit var formatter: Formatter
   // private lateinit var runnable: Runnable
    private var position: Long = 0

    private fun Players(activity: Activity, appDataContainer: AppDataContainer) {
        val prefApi = appDataContainer.prefApi
        player = runBlocking { prefApi.player.get()}
        decode = runBlocking { prefApi.decode(player).get() }
        builder = StringBuilder()
       // runnable = ErrorEvent::timeout
        formatter = Formatter(builder, Locale.getDefault())
        position = C.TIME_UNSET
        createSession(activity)
    }

    private fun createSession(activity: Activity) {
        session = MediaSessionCompat(activity, "TV")
        session!!.setCallback(SessionCallback.create(this))
        session!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        session!!.setSessionActivity(
            PendingIntent.getActivity(
                getContext(),
                0,
                Intent(getContext(), activity.javaClass),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        MediaControllerCompat.setMediaController(activity, session!!.controller)
    }

    fun getUrl(): String {
        return url
    }

    fun getSession(): MediaSessionCompat? {
        return session
    }

    private fun getContext(): Context {
        return ContextProvider.context as Context
    }
}
