package com.calvin.box.movie.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import com.calvin.box.movie.utility.SafeAreaSize

@Composable
internal expect fun SystemAppearance(isDark: Boolean)

expect fun getSafeAreaHeight(): SafeAreaSize
expect fun hideKeyboard(focusManager: FocusManager)
internal expect val DynamicColorsAvailable: Boolean
internal expect val OpenSourceLicenseAvailable: Boolean