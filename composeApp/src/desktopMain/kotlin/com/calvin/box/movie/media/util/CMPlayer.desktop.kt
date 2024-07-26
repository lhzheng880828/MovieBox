package com.calvin.box.movie.media.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.calvin.box.movie.media.model.PlayerSpeed
import network.chaintech.sdpcomposemultiplatform.sdp

@Composable
actual fun CMPPlayer(
    modifier: Modifier,
    url: String,
    isPause: Boolean,
    isMute: Boolean,
    totalTime: (Int) -> Unit,
    currentTime: (Int) -> Unit,
    isSliding: Boolean,
    sliderTime: Int?,
    speed: PlayerSpeed
) {
    Column {
        VideoPlayerImpl(
            url = url,
            modifier = Modifier.fillMaxWidth().height(400.sdp))
    }
}