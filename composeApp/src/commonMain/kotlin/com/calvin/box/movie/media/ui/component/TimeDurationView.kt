package com.calvin.box.movie.media.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.calvin.box.movie.media.extension.formatMinSec
import com.calvin.box.movie.media.model.PlayerConfig

@Composable
fun TimeDurationView(
    playerConfig: PlayerConfig,
    currentTime: Int, // Current playback time in seconds
    totalTime: Int // Total duration of the media in seconds
) {
    // Create a row layout that fills the width of its parent
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween // Distribute space evenly between the child components
    ) {
        // Display the current playback time
        Text(
            modifier = Modifier,
            text = currentTime.formatMinSec(), // Format the current time to "MM:SS" format
            color = playerConfig.durationTextColor,
            style = playerConfig.durationTextStyle
        )
        Spacer(Modifier.weight(1f)) // Add a spacer to push the total time to the right
        // Display the total duration of the media
        Text(
            modifier = Modifier,
            text = totalTime.formatMinSec(), // Format the total time to "MM:SS" format
            color = playerConfig.durationTextColor,
            style = playerConfig.durationTextStyle
        )
    }
}
