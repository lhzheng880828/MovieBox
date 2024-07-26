package com.calvin.box.movie.ui.screens.reels

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.calvin.box.movie.media.model.PlayerConfig
import com.calvin.box.movie.media.ui.reel.ReelsPlayerView
import com.calvin.box.movie.model.MockData
import network.chaintech.sdpcomposemultiplatform.sdp

@Composable
fun ReelsContentView() {
    ReelsPlayerView(
        modifier = Modifier
            .fillMaxSize(),
        urls = MockData().reelsUrlArray,
        playerConfig = PlayerConfig(
            isPauseResumeEnabled  = true,
            isSeekBarVisible = false,
            pauseResumeIconSize = 32.sdp,
            reelVerticalScrolling = true,
            isFastForwardBackwardEnabled = false,
            isMuteControlEnabled = false,
            isSpeedControlEnabled = false,
            isFullScreenEnabled = false,
            isScreenLockEnabled = false
        )
    )
}