package com.calvin.box.movie.media.util

import androidx.compose.runtime.Composable
import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

actual fun formatMinSec(value: Int): String {
    val hour = (value / 3600)
    val remainingSecondsAfterHours = (value % 3600)
    val minutes = remainingSecondsAfterHours / 60
    val seconds = remainingSecondsAfterHours % 60

    val strHour : String = if (hour > 0) { NSString.stringWithFormat(format = "%02d:", hour)
    } else { "" }
    val strMinutes : String = NSString.stringWithFormat(format = "%02d:", minutes)
    val strSeconds : String = NSString.stringWithFormat(format = "%02d", seconds)

    return "${strHour}${strMinutes}${strSeconds}"
}

actual fun formatInterval(value: Int): Int {
    return value
}

@Composable
actual fun LandscapeOrientation(
    isLandscape: Boolean,
    content: @Composable () -> Unit
) {
    content()
}


