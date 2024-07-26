package com.calvin.box.movie.media.util

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
actual fun CMPAudioPlayer(
    modifier: Modifier,
    url: String,
    isPause: Boolean,
    totalTime: (Int) -> Unit,
    currentTime: (Int) -> Unit,
    isSliding: Boolean,
    sliderTime: Int?,
    isRepeat: Boolean,
    loadingState: (Boolean) -> Unit,
    didEndAudio: () -> Unit
) {
    var repeatStatus by remember { mutableStateOf(isRepeat) }

    val context = LocalContext.current
    val exoPlayer = rememberExoPlayer(url, repeatStatus, context)

    LaunchedEffect(isRepeat) {
        repeatStatus = isRepeat
        exoPlayer.repeatMode = if (isRepeat) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
    }

    LaunchedEffect(exoPlayer) {
        while (isActive) {
            currentTime(exoPlayer.currentPosition.coerceAtLeast(0L).toInt())
            delay(1000) // Delay for 1 second
        }
    }

    DisposableEffect(key1 = exoPlayer) {
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                if (!isSliding) {
                    totalTime(player.duration.coerceAtLeast(0L).toInt())
                    currentTime(player.currentPosition.coerceAtLeast(0L).toInt())
                }
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> loadingState(true)
                    Player.STATE_READY -> loadingState(false)
                    Player.STATE_ENDED -> if (repeatStatus.not()) { didEndAudio() }
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(key1 = isPause) {
        exoPlayer.playWhenReady = !isPause
    }

    sliderTime?.let {
        LaunchedEffect(key1 = it) {
            exoPlayer.seekTo(it.toLong())
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun rememberExoPlayer(
    url: String,
    isRepeat: Boolean,
    context: Context,
): ExoPlayer {
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            setHandleAudioBecomingNoisy(true)
        }
    }

    LaunchedEffect(url) {
        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        val dataSourceFactory = DefaultDataSource.Factory(context)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.seekTo(0, 0)
    }

    return exoPlayer
}
