package com.calvin.box.movie.impl

import android.support.v4.media.session.MediaSessionCompat
import com.calvin.box.movie.AndroidPlayers
//import com.fongmi.android.tv.event.ActionEvent

class SessionCallback private constructor(player: AndroidPlayers) : MediaSessionCompat.Callback() {
    private val player: AndroidPlayers = player

    override fun onSeekTo(pos: Long) {
       // player.seekTo(pos)
    }

    override fun onPlay() {
       // ActionEvent.send(ActionEvent.PLAY)
    }

    override fun onPause() {
       // ActionEvent.send(ActionEvent.PAUSE)
    }

    override fun onSkipToPrevious() {
       // ActionEvent.send(ActionEvent.PREV)
    }

    override fun onSkipToNext() {
       // ActionEvent.send(ActionEvent.NEXT)
    }

    companion object {
        fun create(player: AndroidPlayers): SessionCallback {
            return SessionCallback(player)
        }
    }
}

class ActionEvent{

}
