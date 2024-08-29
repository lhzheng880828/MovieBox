package com.calvin.box.movie.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusManager
import com.calvin.box.movie.utility.SafeAreaSize
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle
import platform.UIKit.UIWindow

internal actual val DynamicColorsAvailable: Boolean
    get() = false

internal actual val OpenSourceLicenseAvailable: Boolean
    get() = false

@Composable
actual fun SystemAppearance(isDark:Boolean) {
    LaunchedEffect(isDark) {
        UIApplication.sharedApplication.setStatusBarStyle(
            if (isDark) UIStatusBarStyleDarkContent else UIStatusBarStyleLightContent
        )
    }
}


@OptIn(ExperimentalForeignApi::class)
actual fun getSafeAreaHeight(): SafeAreaSize {
    val window = UIApplication.sharedApplication.windows.first() as UIWindow
    val safeAreaInsets = window.safeAreaInsets
    val topPadding = memScoped {
        val top = safeAreaInsets.getPointer(this)
        top.pointed.top
    }
    val bottomPadding = memScoped {
        val bottom = safeAreaInsets.getPointer(this)
        bottom.pointed.bottom
    }

    return SafeAreaSize(top = topPadding.toFloat(), bottom = bottomPadding.toFloat())
}

actual fun hideKeyboard(focusManager: FocusManager){
    val windows = UIApplication.sharedApplication.windows
    windows.forEach { (it as? UIWindow)?.endEditing(true) }
}