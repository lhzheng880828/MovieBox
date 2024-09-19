package com.calvin.box.movie.bean

import kotlinx.serialization.Serializable

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/9/18
 */
@Serializable
data class PlayMediaInfo(
    val headers: Map<String, String> = emptyMap(),
    val url: String,
    val mimeType: String?=null,
    val drm: Drm?=null,
    val subs: List<Sub> = emptyList(),
    )
