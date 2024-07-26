package com.calvin.box.movie.media.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.exoplayer.ExoPlayer

fun getExoPlayerLifecycleObserver(
    exoPlayer: ExoPlayer,
    isPause: Boolean,
    wasAppInBackground: Boolean,
    setWasAppInBackground: (Boolean) -> Unit
): LifecycleEventObserver =
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (wasAppInBackground)
                    exoPlayer.playWhenReady = !isPause
                setWasAppInBackground(false)
            }

            Lifecycle.Event.ON_PAUSE -> {
                exoPlayer.playWhenReady = false
                setWasAppInBackground(true)
            }

            Lifecycle.Event.ON_STOP -> {
                exoPlayer.playWhenReady = false
                setWasAppInBackground(true)
            }

            Lifecycle.Event.ON_DESTROY -> {
                exoPlayer.release()
            }

            else -> {}
        }
    }
