package com.calvin.box.movie.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import com.calvin.box.movie.utility.SafeAreaSize


@Composable
internal actual fun SystemAppearance(isDark: Boolean) {
    val view = LocalView.current
    LaunchedEffect(isDark) {
        val window = (view.context as Activity).window
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = isDark
            isAppearanceLightNavigationBars = isDark
        }
    }
}


internal actual val DynamicColorsAvailable: Boolean
    get() = true


internal actual val OpenSourceLicenseAvailable: Boolean
    get() = true


actual fun getSafeAreaHeight(): SafeAreaSize {
    return SafeAreaSize()
}