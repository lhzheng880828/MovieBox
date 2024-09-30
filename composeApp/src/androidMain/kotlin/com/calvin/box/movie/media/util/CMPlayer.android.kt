package com.calvin.box.movie.media.util

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import com.calvin.box.movie.bean.PlayMediaInfo
import com.calvin.box.movie.media.model.PlayerSpeed
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
actual fun CMPPlayer(
    modifier: Modifier,
    url: String,
    isPause: Boolean,
    isMute: Boolean,
    totalTime: ((Int) -> Unit),
    currentTime: ((Int) -> Unit),
    isSliding: Boolean,
    sliderTime: Int?,
    speed: PlayerSpeed
) {
    Napier.d { "#CMPPlayer, refresh ui, url: $url" }
    val context = LocalContext.current
    val exoPlayer = rememberExoPlayerWithLifecycle(url, context, isPause)
    val playerView = rememberPlayerView(exoPlayer, context)

    LaunchedEffect(exoPlayer) {
        while (isActive) {
            currentTime(exoPlayer.currentPosition.coerceAtLeast(0L).toInt())
            delay(1000) // Delay for 1 second
        }
    }

    LaunchedEffect(playerView) {
        playerView.keepScreenOn = true
    }

    Box {
        AndroidView(
            factory = { playerView },
            modifier = modifier,
            update = {
                exoPlayer.playWhenReady = !isPause
                exoPlayer.volume = if (isMute) { 0f } else { 1f }
                sliderTime?.let {
                    exoPlayer.seekTo(it.toLong())
                }
                exoPlayer.setPlaybackSpeed(
                    when (speed) {
                        PlayerSpeed.X0_5 -> 0.5f
                        PlayerSpeed.X1 -> 1f
                        PlayerSpeed.X1_5 -> 1.5f
                        PlayerSpeed.X2 -> 2f
                    }
                )
            }
        )

        DisposableEffect(key1 = Unit) {
            val listener = object : Player.Listener {
                override fun onEvents(
                    player: Player, events: Player.Events
                ) {
                    super.onEvents(player, events)
                    if (!isSliding) {
                        totalTime(player.duration.coerceAtLeast(0L).toInt())
                        currentTime(player.currentPosition.coerceAtLeast(0L).toInt())
                    }
                }
            }

            exoPlayer.addListener(listener)

            onDispose {
                exoPlayer.removeListener(listener)
                exoPlayer.release()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            playerView.keepScreenOn = false
        }
    }
}
