package com.calvin.box.movie

// Copyright 2021, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0


import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

//import app.tivi.common.compose.LocalStrings
//import app.tivi.data.models.TraktUser

@Composable
fun UserProfileButton(
    loggedIn: Boolean,
   // user: TraktUser?,
    url: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        when {
            loggedIn && /*user?.avatarUrl*/url != null -> {
                KamelImage(
                    resource = asyncPainterResource(data = url),//user.avatarUrl!!,
                    contentDescription = "",//LocalStrings.current.cdProfilePic(user.name ?: user.username),
                    modifier = Modifier
                        .size(32.dp)
                        .clip(MaterialTheme.shapes.small),
                )
            }
            else -> {
                Icon(
                    imageVector = when {
                        loggedIn -> Icons.Default.Person
                        else -> Icons.Outlined.Person
                    },
                    contentDescription = ""//LocalStrings.current.cdUserProfile,
                )
            }
        }
    }
}