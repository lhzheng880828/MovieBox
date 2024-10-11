package com.calvin.box.movie.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import com.calvin.box.movie.feature.history.HistoryScreenModel
import com.calvin.box.movie.utility.SafeAreaSize

@Composable
internal expect fun SystemAppearance(isDark: Boolean)
expect fun getSafeAreaHeight(): SafeAreaSize
expect fun hideKeyboard(focusManager: FocusManager)
internal expect val DynamicColorsAvailable: Boolean
internal expect val OpenSourceLicenseAvailable: Boolean

@Composable
internal expect fun BackHandler(inSelectionMode: Boolean, viewModel: HistoryScreenModel)