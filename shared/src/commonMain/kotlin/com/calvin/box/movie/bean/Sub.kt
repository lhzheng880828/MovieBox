package com.calvin.box.movie.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sub(
    @SerialName("url")
    val url: String = "",

    @SerialName("name")
    var name: String = "",

    @SerialName("lang")
    val lang: String = "",

    @SerialName("format")
    val format: String = "",

    @SerialName("flag")
    val flag: Int = 0
){
    fun trans() {
        if (Trans.pass()) return
        this.name = Trans.s2t(name)
    }
}