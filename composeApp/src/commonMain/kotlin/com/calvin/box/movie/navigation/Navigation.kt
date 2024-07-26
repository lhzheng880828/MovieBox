package com.calvin.box.movie.navigation

import androidx.compose.runtime.compositionLocalOf
import com.calvin.box.movie.model.VideoModel

interface Navigation {
    fun back(): Boolean
    fun backToRoot(): Boolean

    fun goToVideoPlayerScreen(currentVideo: VideoModel): Boolean
}

val LocalNavigation = compositionLocalOf<Navigation> { error("navigation failure") }