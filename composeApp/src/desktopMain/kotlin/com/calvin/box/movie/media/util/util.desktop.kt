package com.calvin.box.movie.media.util

import androidx.compose.runtime.Composable
import java.util.concurrent.TimeUnit

actual fun formatMinSec(value: Int): String {
    return if (value == 0) {
        "00:00"
    } else {
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(value.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(value.toLong()) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(value.toLong())
            )
        )
    }
}

actual fun formatInterval(value: Int): Int {
    return value * 1000
}

@Composable
actual fun LandscapeOrientation(isLandscape: Boolean, content: @Composable () -> Unit) {
}