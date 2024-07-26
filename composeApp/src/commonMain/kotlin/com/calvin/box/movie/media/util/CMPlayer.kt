package com.calvin.box.movie.media.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.calvin.box.movie.media.model.PlayerSpeed

@Composable
expect fun CMPPlayer(
    modifier: Modifier,
    url: String,
    isPause: Boolean,
    isMute: Boolean,
    totalTime: ((Int) -> Unit),
    currentTime: ((Int) -> Unit),
    isSliding: Boolean,
    sliderTime: Int?,
    speed: PlayerSpeed
)


