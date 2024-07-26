package com.calvin.box.movie.media.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import com.calvin.box.movie.media.model.PlayerSpeed
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.AVQueuePlayer
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.muted
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.playbackLikelyToKeepUp
import platform.AVFoundation.rate
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVKit.AVPlayerViewController
import platform.CoreGraphics.CGRect
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSSelectorFromString
import platform.Foundation.NSURL
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIApplication
import platform.UIKit.UIView
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
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
)  {
    val playerItem = remember { mutableStateOf<AVPlayerItem?>(null) }
    val player: AVQueuePlayer by remember { mutableStateOf(AVQueuePlayer(playerItem.value)) }
    val playerLayer by remember { mutableStateOf(AVPlayerLayer()) }
    val avPlayerViewController = remember { AVPlayerViewController() }
    avPlayerViewController.player = player
    avPlayerViewController.showsPlaybackControls = false
    avPlayerViewController.videoGravity = AVLayerVideoGravityResizeAspectFill
    val playerContainer = UIView().apply {
        layer.addSublayer(playerLayer)
    }
    player.muted = isMute

    var isLoading by remember { mutableStateOf(true) }

    fun setPlayerRate(speed: PlayerSpeed) {
        player.rate = when (speed) {
            PlayerSpeed.X0_5 -> 0.5f
            PlayerSpeed.X1 -> 1f
            PlayerSpeed.X1_5 -> 1.5f
            PlayerSpeed.X2 -> 2f
        }
    }

    LaunchedEffect(url) {
        val urlObject = NSURL.URLWithString(url)
        val newItem = urlObject?.let { AVPlayerItem(uRL = it) }
        playerItem.value = newItem
        playerItem.value?.let {
            player.replaceCurrentItemWithPlayerItem(it)
        }
        if (isPause) { player.pause() } else {
            player.play()
            setPlayerRate(speed)
        }
    }

    Box {
        UIKitView(
            factory = {
                playerContainer.addSubview(avPlayerViewController.view)
                playerContainer
            },
            onResize = { view: UIView, rect: CValue<CGRect> ->
                CATransaction.begin()
                CATransaction.setValue(true, kCATransactionDisableActions)
                view.layer.setFrame(rect)
                playerLayer.setFrame(rect)
                avPlayerViewController.view.layer.frame = rect
                CATransaction.commit()
            },
            update = { _ ->
                MainScope().launch {
                    if (isPause) { player.pause() } else { player.play() }
                    UIApplication.sharedApplication.idleTimerDisabled = isPause.not()
                    sliderTime?.let {
                        val time = CMTimeMakeWithSeconds(it.toDouble(), 1)
                        player.seekToTime(time)
                    }
                    if (isPause.not()) {
                        setPlayerRate(speed)
                    }
                }
            },
            interactive = false,
            modifier = modifier
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Red
                )
            }
        }
    }

    DisposableEffect(Unit) {
        val observerObject = object : NSObject() {
            @ObjCAction
            fun onPlayerItemDidPlayToEndTime() {
                player.currentItem?.let { item ->
                    player.seekToTime(CMTimeMakeWithSeconds(0.0, 1))
                    player.removeItem(item)
                    player.insertItem(item, afterItem = null)
                    player.play()
                }
            }
        }

        val timeObserver = player.addPeriodicTimeObserverForInterval(
            CMTimeMakeWithSeconds(1.0, 1),
            null
        ) { _ ->
            if (!isSliding) {
                MainScope().launch {
                    val duration = player.currentItem?.duration?.let { CMTimeGetSeconds(it) } ?: 0.0
                    val current = CMTimeGetSeconds(player.currentTime())
                    currentTime(current.toInt())
                    totalTime(duration.toInt())
                    isLoading = player.currentItem?.playbackLikelyToKeepUp?.not() ?: false
                }
            }
        }

        NSNotificationCenter.defaultCenter().addObserver(
            observerObject,
            NSSelectorFromString("onPlayerItemDidPlayToEndTime"),
            AVPlayerItemDidPlayToEndTimeNotification,
            player.currentItem
        )

        onDispose {
            UIApplication.sharedApplication.idleTimerDisabled = false
            player.pause()
            player.replaceCurrentItemWithPlayerItem(null)
            NSNotificationCenter.defaultCenter().removeObserver(observerObject)
            player.removeTimeObserver(timeObserver)
        }
    }


}