package com.calvin.box.movie.navigation

import androidx.compose.runtime.compositionLocalOf
import cafe.adriel.voyager.core.screen.Screen
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.model.VideoModel

interface ScreenContainer {
    fun goToVideoPlayerScreen(currentVideo: VideoModel): Screen
    fun goToDetailScreen(currentVideo: VideoModel):Screen
    fun goToFolderScreen(key: String, result: Result): Screen
}

val LocalScreenContainer =
    compositionLocalOf<ScreenContainer> { error("screen container not found") }
