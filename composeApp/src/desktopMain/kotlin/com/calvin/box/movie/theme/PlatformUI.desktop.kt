package com.calvin.box.movie.theme


import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import com.calvin.box.movie.utility.SafeAreaSize

@Composable
internal actual fun SystemAppearance(isDark: Boolean) {
}

actual fun getSafeAreaHeight(): SafeAreaSize {
    return SafeAreaSize()
}

actual fun hideKeyboard(focusManager: FocusManager){
    focusManager.clearFocus()
}

internal actual val DynamicColorsAvailable: Boolean
    get() = false

internal actual val OpenSourceLicenseAvailable: Boolean
    get() = false