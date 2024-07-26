package com.calvin.box.movie.media.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

data class PlayerConfig @OptIn(ExperimentalResourceApi::class) constructor(
    var isPauseResumeEnabled: Boolean = true,
    var isSeekBarVisible: Boolean = true,
    var isDurationVisible: Boolean = true,
    var seekBarThumbColor: Color = Color.Red,
    var seekBarActiveTrackColor: Color = Color.White,
    var seekBarInactiveTrackColor: Color = Color.Black.copy(alpha = 0.4f),
    var durationTextColor: Color = Color.White,
    var durationTextStyle: TextStyle = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal
    ),
    var seekBarBottomPadding: Dp = 10.dp,
    var playIconResource: DrawableResource? = null,
    var pauseIconResource: DrawableResource? = null,
    var pauseResumeIconSize: Dp = 40.dp,
    var reelVerticalScrolling: Boolean = true,
    var isAutoHideControlEnabled:Boolean = true,
    var controlHideIntervalSeconds: Int = 3,  //Seconds
    var isFastForwardBackwardEnabled: Boolean = true,
    var fastForwardBackwardIconSize: Dp = 40.dp,
    var fastForwardIconResource: DrawableResource? = null,
    var fastBackwardIconResource: DrawableResource? = null,
    var fastForwardBackwardIntervalSeconds: Int = 10,  //Seconds
    var isMuteControlEnabled: Boolean = true,
    var unMuteIconResource: DrawableResource? = null,
    var muteIconResource: DrawableResource? = null,
    var topControlSize: Dp = 30.dp,
    var isSpeedControlEnabled: Boolean = true,
    var speedIconResource: DrawableResource? = null,
    var isFullScreenEnabled: Boolean = true,
    var controlTopPadding: Dp = 15.dp,
    var isScreenLockEnabled: Boolean = true,
    var iconsTintColor: Color = Color.White
    )