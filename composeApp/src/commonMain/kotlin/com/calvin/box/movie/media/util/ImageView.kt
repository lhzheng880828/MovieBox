package com.calvin.box.movie.media.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun ImageFromUrl(
    modifier: Modifier,
    data: Any,
    contentScale: ContentScale = ContentScale.Crop,
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        val painter = asyncPainterResource(data = data)

        KamelImage(
            resource = painter,
            contentDescription = null,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize(),
            onLoading = {
                // 显示加载中的占位符
            },
            onFailure = {
                // 显示加载失败的占位符
            }
        )
    }
}