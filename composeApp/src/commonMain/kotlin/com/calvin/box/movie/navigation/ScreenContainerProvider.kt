package com.calvin.box.movie.navigation

import cafe.adriel.voyager.core.screen.Screen
import com.calvin.box.movie.model.VideoModel
import com.calvin.box.movie.ui.screens.videoplayerview.WrapVideoPlayerView


class ScreenContainerProvider : ScreenContainer {
    override fun goToVideoPlayerScreen(currentVideo: VideoModel): Screen = WrapVideoPlayerView(currentVideo)
}

