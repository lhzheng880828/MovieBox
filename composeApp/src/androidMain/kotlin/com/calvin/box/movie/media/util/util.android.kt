package com.calvin.box.movie.media.util

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
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
actual fun LandscapeOrientation(
    isLandscape: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    DisposableEffect(isLandscape) {
        activity?.requestedOrientation = if (isLandscape) { ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE } else { ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    content()
}