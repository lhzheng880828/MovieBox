package com.calvin.box.movie.media.ui.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import com.calvin.box.movie.bean.PlayMediaInfo
import com.calvin.box.movie.media.extension.formattedInterval
import com.calvin.box.movie.media.model.PlayerConfig
import com.calvin.box.movie.media.model.PlayerSpeed
import com.calvin.box.movie.media.model.gradientBGColors
import com.calvin.box.movie.media.ui.component.SpeedSelectionView
import com.calvin.box.movie.media.ui.video.controls.BottomControlView
import com.calvin.box.movie.media.ui.video.controls.CenterControlView
import com.calvin.box.movie.media.ui.video.controls.LockScreenView
import com.calvin.box.movie.media.ui.video.controls.TopControlView
import com.calvin.box.movie.media.util.CMPPlayer
import com.calvin.box.movie.media.util.setPlayMediaInfo
import io.github.aakira.napier.Napier

@Composable
fun VideoPlayerWithControl(
    modifier: Modifier,
    url: String, // URL of the video
    playMediaInfo: PlayMediaInfo,
    playerConfig: PlayerConfig, // Configuration for the player
    isPause: Boolean, // Flag indicating if the video is paused
    onPauseToggle: (() -> Unit), // Callback for toggling pause/resume
    showControls: Boolean, // Flag indicating if controls should be shown
    onShowControlsToggle: (() -> Unit), // Callback for toggling show/hide controls
    onChangeSeekbar: ((Boolean) -> Unit), // Callback for seek bar sliding
    isFullScreen: Boolean,
    onFullScreenToggle: (() -> Unit)
) {
    Napier.d { "#VideoPlayerWithControl, refresh ui, url: $url" }
    var totalTime by remember { mutableStateOf(0) } // Total duration of the video
    var currentTime by remember { mutableStateOf(0) } // Current playback time
    var isSliding by remember { mutableStateOf(false) } // Flag indicating if the seek bar is being slid
    var sliderTime: Int? by remember { mutableStateOf(null) } // Time indicated by the seek bar
    var isMute by remember { mutableStateOf(false) } // Flag indicating if the audio is muted
    var selectedSpeed by remember { mutableStateOf(PlayerSpeed.X1) } // Selected playback speed
    var showSpeedSelection by remember { mutableStateOf(false) } // Selected playback speed
    var isScreenLocked by remember { mutableStateOf(false) }

    setPlayMediaInfo(playMediaInfo)
    // Container for the video player and control components
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    onShowControlsToggle() // Toggle show/hide controls on tap
                    showSpeedSelection = false
                }
            }
    ) {
        // Video player component
        CMPPlayer(
            modifier = modifier,
            url = url,
            isPause = isPause,
            isMute = isMute,
            totalTime = { totalTime = it }, // Update total time of the video
            currentTime = {
                if (isSliding.not()) {
                    currentTime = it // Update current playback time
                    sliderTime = null // Reset slider time if not sliding
                }
            },
            isSliding = isSliding, // Pass seek bar sliding state
            sliderTime = sliderTime, // Pass seek bar slider time
            speed = selectedSpeed // Pass selected playback speed
        )

        if (isScreenLocked.not()) {
            // Top control view for playback speed and mute/unMute
            TopControlView(
                playerConfig = playerConfig,
                isMute = isMute,
                onMuteToggle = { isMute = isMute.not() }, // Toggle mute/unMute
                showControls = showControls, // Pass show/hide controls state
                onTapSpeed = { showSpeedSelection = showSpeedSelection.not() },
                isFullScreen = isFullScreen,
                onFullScreenToggle = { onFullScreenToggle() },
                onLockScreenToggle = { isScreenLocked = isScreenLocked.not()}
            )

            // Center control view for pause/resume and fast forward/backward actions
            CenterControlView(
                playerConfig = playerConfig,
                isPause = isPause,
                onPauseToggle = onPauseToggle,
                onBackwardToggle = { // Seek backward
                    isSliding = true
                    val newTime =
                        currentTime - playerConfig.fastForwardBackwardIntervalSeconds.formattedInterval()
                    sliderTime = if (newTime < 0) {
                        0
                    } else {
                        newTime
                    }
                    isSliding = false
                },
                onForwardToggle = { // Seek forward
                    isSliding = true
                    val newTime =
                        currentTime + playerConfig.fastForwardBackwardIntervalSeconds.formattedInterval()
                    sliderTime = if (newTime > totalTime) {
                        totalTime
                    } else {
                        newTime
                    }
                    isSliding = false
                },
                showControls = showControls
            )

            // Bottom control view for seek bar and time duration display
            BottomControlView(
                playerConfig = playerConfig,
                currentTime = currentTime, // Pass current playback time
                totalTime = totalTime, // Pass total duration of the video
                showControls = showControls, // Pass show/hide controls state
                onChangeSliderTime = { sliderTime = it }, // Update seek bar slider time
                onChangeCurrentTime = { currentTime = it }, // Update current playback time
                onChangeSliding = { // Update seek bar sliding state
                    isSliding = it
                    onChangeSeekbar(isSliding)
                }
            )
        } else {
            if (playerConfig.isScreenLockEnabled) {
                LockScreenView(
                    playerConfig = playerConfig,
                    showControls = showControls,
                    onLockScreenToggle = { isScreenLocked = isScreenLocked.not()}
                )
            }
        }

        Box {
            //Playback speed options popup
            AnimatedVisibility(
                modifier = Modifier,
                visible = showSpeedSelection,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(durationMillis = 700))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush = Brush.horizontalGradient(gradientBGColors))
                ) {

                }
            }

            //Playback speed options popup
            AnimatedVisibility(
                modifier = Modifier,
                visible = showSpeedSelection,
                enter = slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth }, // Start from the right edge
                    animationSpec = tween(durationMillis = 500) // Animation duration
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth }, // Slide out to the right edge
                    animationSpec = tween(durationMillis = 500) // Animation duration
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    SpeedSelectionView(
                        buttonSize = (playerConfig.topControlSize * 1.25f),
                        selectedSpeed = selectedSpeed,
                        onSelectSpeed = {
                            it?.let {
                                selectedSpeed = it
                            }
                            showSpeedSelection = false
                        }
                    )
                }
            }
        }
    }
}
