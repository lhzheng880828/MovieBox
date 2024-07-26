package com.calvin.box.movie.media.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.calvin.box.movie.media.model.PlayerSpeed
import com.calvin.box.movie.media.model.selectedSpeedButtonColor
import com.calvin.box.movie.media.model.selectedTextColor
import com.calvin.box.movie.media.model.unselectedSpeedButtonColor
import com.calvin.box.movie.media.model.unselectedTextColor
import kotlinx.coroutines.delay

@Composable
fun SpeedSelectionView(
    buttonSize: Dp,
    selectedSpeed: PlayerSpeed,
    onSelectSpeed: ((PlayerSpeed?) -> Unit)
) {
    LaunchedEffect(selectedSpeed) {
        delay(5000) // Wait for 5 seconds
        onSelectSpeed(null)
    }

    Column (
        modifier = Modifier.fillMaxSize()
    ){
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {

            Column(modifier = Modifier.fillMaxHeight()
                .padding(horizontal = 35.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp, alignment = Alignment.CenterVertically)
            ) {


                PlayerSpeedButton(
                    title = "0.5x",
                    size = buttonSize,
                    backgroundColor = if (selectedSpeed == PlayerSpeed.X0_5) {
                        selectedSpeedButtonColor
                    } else {
                        unselectedSpeedButtonColor
                    },
                    titleColor = if (selectedSpeed == PlayerSpeed.X0_5) {
                        selectedTextColor
                    } else {
                        unselectedTextColor
                    },
                    onClick = { onSelectSpeed(PlayerSpeed.X0_5) }
                )

                PlayerSpeedButton(
                    title = "1.0x",
                    size = buttonSize,
                    backgroundColor = if (selectedSpeed == PlayerSpeed.X1) {
                        selectedSpeedButtonColor
                    } else {
                        unselectedSpeedButtonColor
                    },
                    titleColor = if (selectedSpeed == PlayerSpeed.X1) {
                        selectedTextColor
                    } else {
                        unselectedTextColor
                    },
                    onClick = { onSelectSpeed(PlayerSpeed.X1) }
                )

                PlayerSpeedButton(
                    title = "1.5x",
                    size = buttonSize,
                    backgroundColor = if (selectedSpeed == PlayerSpeed.X1_5) {
                        selectedSpeedButtonColor
                    } else {
                        unselectedSpeedButtonColor
                    },
                    titleColor = if (selectedSpeed == PlayerSpeed.X1_5) {
                        selectedTextColor
                    } else {
                        unselectedTextColor
                    },
                    onClick = { onSelectSpeed(PlayerSpeed.X1_5) }
                )

                PlayerSpeedButton(
                    title = "2.0x",
                    size = buttonSize,
                    backgroundColor = if (selectedSpeed == PlayerSpeed.X2) {
                        selectedSpeedButtonColor
                    } else {
                        unselectedSpeedButtonColor
                    },
                    titleColor = if (selectedSpeed == PlayerSpeed.X2) {
                        selectedTextColor
                    } else {
                        unselectedTextColor
                    },
                    onClick = { onSelectSpeed(PlayerSpeed.X2) }
                )
            }
        }
    }
}