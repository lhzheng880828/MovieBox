package com.calvin.box.movie.theme

import androidx.compose.runtime.Composable
import com.calvin.box.movie.utility.SafeAreaSize

@Composable
internal expect fun SystemAppearance(isDark: Boolean)

expect fun getSafeAreaHeight(): SafeAreaSize

internal expect val DynamicColorsAvailable: Boolean
internal expect val OpenSourceLicenseAvailable: Boolean