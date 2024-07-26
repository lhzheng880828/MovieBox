package com.calvin.box.movie.utility

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.calvin.box.movie.theme.getSafeAreaHeight
import network.chaintech.sdpcomposemultiplatform.sdp

val BottomNavigationBarHeight : Dp
    @Composable
    get() = 65.sdp

data class SafeAreaSize(
    var top: Float = 0f,
    var bottom: Float = 0f,
)


fun getSafeAreaSize(): SafeAreaSize {
    return getSafeAreaHeight()
}
