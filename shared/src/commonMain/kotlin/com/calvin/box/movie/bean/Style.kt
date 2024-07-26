package com.calvin.box.movie.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
data class Style(
    @SerialName("type") private var type: String = "",
    @SerialName("ratio") private var ratio: Float = 0f
) {

    companion object {
        fun rect(): Style {
            return Style("rect", 0.75f)
        }

        fun list(): Style {
            return Style("list")
        }

        fun get(land: Int, circle: Int, ratio: Float): Style? {
            return if (land == 1) Style("rect", if (ratio == 0f) 1.33f else ratio)
            else if (circle == 1) Style("oval", if (ratio == 0f) 1.0f else ratio)
            else null
        }
    }


    fun getRatio(): Float {
        return if (ratio <= 0) {
            if (isOval()) 1.0f else 0.75f
        } else {
            min(4f, ratio)
        }
    }

    fun isRect(): Boolean {
        return "rect" ==  type
    }

    fun isOval(): Boolean {
        return "oval" ==type
    }

    fun isList(): Boolean {
        return "list" ==  type
    }

    fun isLand(): Boolean {
        return isRect() && getRatio() > 1.0f
    }

    fun getViewType(): Int {
        return when ( type) {
            "oval" -> ViewType.OVAL
            "list" -> ViewType.LIST
            else -> ViewType.RECT
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Style) return false
        val it = other as Style
        return  type == it.type && getRatio() == it.getRatio()
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + ratio.hashCode()
        return result
    }

}
object ViewType {
    const val RECT: Int = 0
    const val OVAL: Int = 1
    const val LIST: Int = 2
}