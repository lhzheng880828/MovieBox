package com.calvin.box.movie.media.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CMPAudioPlayer(
    modifier: Modifier,
    url: String,
    isPause: Boolean,
    totalTime: ((Int) -> Unit),
    currentTime: ((Int) -> Unit),
    isSliding: Boolean,
    sliderTime: Int?,
    isRepeat: Boolean,
    loadingState: (Boolean) -> Unit,
    didEndAudio: () -> Unit
)