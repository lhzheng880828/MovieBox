package com.calvin.box.movie.media.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import uk.co.caprica.vlcj.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.MediaPlayer
//import uk.co.caprica.vlcj.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent
import java.awt.Component
import java.util.Locale

@Composable
 fun VideoPlayerImpl(
    url: String,
    modifier: Modifier,
) {
    val mediaPlayerComponent = remember { initializeMediaPlayerComponent() }
    val mediaPlayer = remember { mediaPlayerComponent.mediaPlayer() }

    val factory = remember { { mediaPlayerComponent } }
    /* OR the following code and using SwingPanel(factory = { factory }, ...) */
    // val factory by rememberUpdatedState(mediaPlayerComponent)

    LaunchedEffect(url) { mediaPlayer.playMedia/*OR .start*/(url) }
    DisposableEffect(Unit) { onDispose(mediaPlayer::release) }
    SwingPanel(
        factory = factory,
        background = Color.Transparent,
        modifier = modifier
    )
}

private fun initializeMediaPlayerComponent(): Component {
    NativeDiscovery().discover()
    /*return if (isMacOS()) {
        DirectMediaPlayerComponent()
    } else {
        EmbeddedMediaPlayerComponent()
    }*/
    return EmbeddedMediaPlayerComponent()
}


/**
 * Returns [MediaPlayer] from player components.
 * The method names are the same, but they don't share the same parent/interface.
 * That's why we need this method.
 */
private fun Component.mediaPlayer() = when (this) {
    //is CallbackMediaPlayerComponent -> mediaPlayer()
    is EmbeddedMediaPlayerComponent -> getMediaPlayer()
    else -> error("mediaPlayer() can only be called on vlcj player components")
}

private fun isMacOS(): Boolean {
    val os = System
        .getProperty("os.name", "generic")
        .lowercase(Locale.ENGLISH)
    return "mac" in os || "darwin" in os
}