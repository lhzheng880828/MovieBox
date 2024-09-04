package com.calvin.box.movie.feature.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/9/4
 */
inline fun LazyListScope.itemSpacer(height: Dp) {
    item {
        Spacer(
            Modifier
                .height(height)
                .fillParentMaxWidth(),
        )
    }
}

