package com.calvin.box.movie.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import com.calvin.box.movie.feature.history.HistoryScreenModel
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

@Composable
internal actual fun BackHandler(inSelectionMode: Boolean, viewModel: HistoryScreenModel) {
    androidx.activity.compose.BackHandler {
        if (inSelectionMode) {
            viewModel.exitSelectionMode()
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

actual fun hideKeyboard(focusManager: FocusManager){
    focusManager.clearFocus()
}