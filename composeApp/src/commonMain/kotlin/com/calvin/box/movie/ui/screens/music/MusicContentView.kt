package com.calvin.box.movie.ui.screens.music

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.calvin.box.movie.media.model.AudioPlayerConfig
import com.calvin.box.movie.media.ui.audio.AudioPlayerView
import com.calvin.box.movie.model.MockData
import network.chaintech.sdpcomposemultiplatform.sdp

@Composable
fun MusicContentView() {
    AudioPlayerView(
        modifier = Modifier.fillMaxSize(),
        audios = MockData().audioFilesArray,
        audioPlayerConfig = AudioPlayerConfig(
            controlsBottomPadding = 30.sdp
        )
    )
}