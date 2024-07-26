package com.calvin.box.movie.theme


import androidx.compose.runtime.Composable
import com.calvin.box.movie.utility.SafeAreaSize

@Composable
internal actual fun SystemAppearance(isDark: Boolean) {
}

actual fun getSafeAreaHeight(): SafeAreaSize {
    return SafeAreaSize()
}

internal actual val DynamicColorsAvailable: Boolean
    get() = false

internal actual val OpenSourceLicenseAvailable: Boolean
    get() = false