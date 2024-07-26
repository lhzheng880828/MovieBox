package com.calvin.box.movie.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cate(
    @SerialName("land")
    val land: Int = 0,

    @SerialName("circle")
    val circle: Int = 0,

    @SerialName("ratio")
    val ratio: Float = 0f
)   {

    fun getStyle(): Style? = Style.get(land, circle, ratio)
}