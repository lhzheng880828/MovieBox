package com.calvin.box.movie.xlab.paging

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/7/29
 */

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Composable
fun RepoSearchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = if (darkTheme) darkColors() else lightColors(),
        content = content,
    )
}