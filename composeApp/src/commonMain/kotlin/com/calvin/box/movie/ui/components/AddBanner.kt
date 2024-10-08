package com.calvin.box.movie.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.calvin.box.movie.font.FontType
import com.calvin.box.movie.font.MediaFont
import com.calvin.box.movie.utility.FromRemote
import network.chaintech.sdpcomposemultiplatform.sdp
import network.chaintech.sdpcomposemultiplatform.ssp

@Composable
fun AddBanner(
    title: String,
    image: String,
    padding: Dp = 12.sdp) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = padding),
        verticalArrangement = Arrangement.spacedBy(16.sdp),
        horizontalAlignment = Alignment.Start,

    ) {
        Text(
            text = title,
            style = MediaFont.lexendDeca(
                size = FontType.SubHeading,
                type = MediaFont.LexendDeca.Medium
            ),
            fontSize = 14.ssp,
            fontWeight = FontWeight.Bold,
            //color = MyApplicationTheme.colors.white,
            modifier = Modifier,
        )

        Card(shape = RoundedCornerShape(7.sdp)) {
            FromRemote(
                painterResource = image,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.sdp)
                    //.background(MyApplicationTheme.colors.border)
            )
        }
    }
}
