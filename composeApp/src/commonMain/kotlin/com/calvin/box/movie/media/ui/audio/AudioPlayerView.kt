package com.calvin.box.movie.media.ui.audio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.RepeatOneOn
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.calvin.box.movie.media.extension.formatMinSec
import com.calvin.box.movie.media.model.AudioFile
import com.calvin.box.movie.media.model.AudioPlayerConfig
import com.calvin.box.movie.media.ui.component.AnimatedClickableIcon
import com.calvin.box.movie.media.util.CMPAudioPlayer
import com.calvin.box.movie.media.util.ImageFromUrl
import kotlin.random.Random


@Composable
fun AudioPlayerView(
    modifier: Modifier = Modifier, // Modifier for the composable
    audios: List<AudioFile>, // URL of the video
    audioPlayerConfig: AudioPlayerConfig = AudioPlayerConfig(), // Configuration for the player
    currentItemIndex: ((Int) -> Unit)? = null
){
    var isPause by remember { mutableStateOf(false) } // State for pausing/resuming audio
    var totalTime by remember { mutableStateOf(0) } // Total duration of the audio
    var currentTime by remember { mutableStateOf(0) } // Current playback time
    var isSliding by remember { mutableStateOf(false) } // Flag indicating if the seek bar is being slid
    var sliderTime: Int? by remember { mutableStateOf(null) } // Time indicated by the seek bar
    var isLoading by remember { mutableStateOf(true) } // Flag indicating audio buffer
    var currentIndex by remember { mutableStateOf(0) }
    var isShuffle by remember { mutableStateOf(false) } // State for Shuffle
    var isRepeat by remember { mutableStateOf(false) } // State for repeat one

    fun changeAudio(isNext: Boolean) {
        fun getNextShuffleIndex(): Int {
            if (audios.size <= 1) return Random.nextInt(0, audios.size)
            var newIndex: Int
            do {
                newIndex = Random.nextInt(0, audios.size)
            } while (newIndex == currentIndex)
            return newIndex
        }

        if (isNext) {
            currentIndex = if (isShuffle) {
                getNextShuffleIndex()
            } else {
                (currentIndex + 1) % audios.size
            }
        }else {
            if(currentIndex > 0) {
                currentIndex -= 1
            } else {
                isSliding = true
                sliderTime = 0
                isSliding = false
            }
        }
        isPause = false
        isLoading = true
    }

    LaunchedEffect(currentIndex) {
        currentItemIndex?.let {
            it(currentIndex)
        }
    }

    // Container for the audio player and control components
    Box(
        modifier = modifier
            .background(audioPlayerConfig.backgroundColor)
    ) {

        if(audios.isEmpty().not()) {
            // Audio player component
            CMPAudioPlayer(
                modifier = modifier,
                url = audios[currentIndex].audioUrl,
                isPause = isPause,
                totalTime = { totalTime = it }, // Update total time of the audio
                currentTime = {
                    if (isSliding.not()) {
                        currentTime = it // Update current playback time
                        sliderTime = null // Reset slider time if not sliding
                    }
                },
                isSliding = isSliding, // Pass seek bar sliding state
                sliderTime = sliderTime, // Pass seek bar slider time,
                isRepeat = isRepeat,
                loadingState = { isLoading = it },
                didEndAudio = {
                    changeAudio(true)
                }
            )
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart) // Align the column to the bottom
                    .padding(bottom = audioPlayerConfig.controlsBottomPadding),
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth()
                        .weight(0.5f)
                        .padding(horizontal = 25.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = modifier.weight(0.25f))
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .fillMaxHeight(0.7f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = audioPlayerConfig.coverBackground, shape = RoundedCornerShape(10.dp)) ,
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Music Note",
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize(0.8f)
                        )

                        if(audios[currentIndex].thumbnailUrl.isEmpty().not()) {
                            ImageFromUrl(
                                modifier = Modifier
                                    .fillMaxSize(),
                                data = audios[currentIndex].thumbnailUrl,
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }

                    if (audios[currentIndex].audioTitle.isEmpty().not()) {
                        Text(
                            modifier = Modifier.padding(top = 25.dp),
                            text = audios[currentIndex].audioTitle, // Format the current time to "MM:SS" format
                            color = audioPlayerConfig.fontColor,
                            style = audioPlayerConfig.titleTextStyle,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = modifier.weight(0.05f))
                }


                if(audioPlayerConfig.isControlsVisible) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f) // Occupy remaining horizontal space
                        ) {
                            // Slider for seeking through the media
                            Slider(
                                modifier = Modifier.fillMaxWidth().height(25.dp),
                                value = currentTime.toFloat(), // Current value of the slider
                                onValueChange = {
                                    currentTime = it.toInt()
                                    isSliding = true
                                    sliderTime = null
                                },
                                valueRange = 0f..totalTime.toFloat(), // Range of the slider
                                onValueChangeFinished = {
                                    isSliding = false
                                    sliderTime = currentTime
                                },
                                colors = SliderDefaults.colors(
                                    thumbColor = audioPlayerConfig.seekBarThumbColor,
                                    inactiveTrackColor = audioPlayerConfig.seekBarInactiveTrackColor,
                                    activeTrackColor = audioPlayerConfig.seekBarActiveTrackColor
                                )
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth().
                                    padding(top = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween // Distribute space evenly between the child components
                            ) {
                                // Display the current playback time
                                Text(
                                    modifier = Modifier,
                                    text = currentTime.formatMinSec(), // Format the current time to "MM:SS" format
                                    color = audioPlayerConfig.fontColor,
                                    style = audioPlayerConfig.durationTextStyle
                                )
                                Spacer(Modifier.weight(1f)) // Add a spacer to push the total time to the right
                                // Display the total duration of the media
                                Text(
                                    modifier = Modifier,
                                    text = totalTime.formatMinSec(), // Format the total time to "MM:SS" format
                                    color = audioPlayerConfig.fontColor,
                                    style = audioPlayerConfig.durationTextStyle
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().height(max(audioPlayerConfig.pauseResumeIconSize, audioPlayerConfig.previousNextIconSize)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AnimatedClickableIcon(
                            painterRes = if (isRepeat) audioPlayerConfig.repeatOnIconResource else audioPlayerConfig.repeatOffIconResource,
                            imageVector = if (isRepeat) Icons.Filled.RepeatOneOn else Icons.Filled.RepeatOne,
                            contentDescription = "Repeat",
                            tint = audioPlayerConfig.iconsTintColor,
                            iconSize = audioPlayerConfig.advanceControlIconSize,
                            onClick = {
                                isRepeat = isRepeat.not()
                            }
                        )

                        // If fast backward is enabled and an icon is provided, show the backward icon
                        AnimatedClickableIcon(
                            painterRes = audioPlayerConfig.previousIconResource,
                            imageVector = Icons.Filled.SkipPrevious,
                            contentDescription = "Previous",
                            tint = audioPlayerConfig.iconsTintColor,
                            iconSize = audioPlayerConfig.previousNextIconSize,
                            onClick = {
                                changeAudio(false)
                            }
                        )

                        Box {
                            // If pause/resume is enabled and icons are provided, show the appropriate icon
                            AnimatedClickableIcon(
                                painterRes = if (isPause) audioPlayerConfig.playIconResource else audioPlayerConfig.pauseIconResource,
                                imageVector = if (isPause) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                                contentDescription = "Play/Pause",
                                tint = audioPlayerConfig.iconsTintColor,
                                iconSize = audioPlayerConfig.pauseResumeIconSize,
                                onClick = { isPause = isPause.not() }
                            )

                            if (isLoading) {
                                Box(modifier = Modifier.size(audioPlayerConfig.pauseResumeIconSize)) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center).fillMaxSize(),
                                        color = audioPlayerConfig.loadingIndicatorColor
                                    )
                                }
                            }
                        }

                        AnimatedClickableIcon(
                            painterRes = audioPlayerConfig.nextIconResource,
                            imageVector = Icons.Filled.SkipNext,
                            contentDescription = "Next",
                            tint = audioPlayerConfig.iconsTintColor,
                            iconSize = audioPlayerConfig.previousNextIconSize,
                            onClick = {
                                changeAudio(isNext = true)
                            }
                        )

                        AnimatedClickableIcon(
                            painterRes = if (isShuffle) audioPlayerConfig.shuffleOnIconResource else audioPlayerConfig.shuffleOffIconResource,
                            imageVector = if (isShuffle) Icons.Filled.ShuffleOn else Icons.Filled.Shuffle,
                            contentDescription = "Shuffle",
                            tint = audioPlayerConfig.iconsTintColor,
                            iconSize = audioPlayerConfig.advanceControlIconSize,
                            onClick = {
                                isShuffle = isShuffle.not()
                            }
                        )
                    }
                }
            }
        }
    }
}
