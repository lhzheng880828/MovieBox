package com.calvin.box.movie.navigation

import cafe.adriel.voyager.core.screen.Screen
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.feature.detail.VodDetailScreen
import com.calvin.box.movie.feature.folder.FolderScreen
import com.calvin.box.movie.model.VideoModel
import com.calvin.box.movie.feature.videoplayerview.WrapVideoPlayerView


class ScreenContainerProvider : ScreenContainer {
    override fun goToVideoPlayerScreen(currentVideo: VideoModel): Screen = WrapVideoPlayerView(currentVideo)
    override fun goToDetailScreen(currentVideo: VideoModel): Screen  = VodDetailScreen(currentVideo)
    override fun goToFolderScreen(key: String, result: Result): Screen  = FolderScreen(key, result)

}

