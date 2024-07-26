package com.calvin.box.movie.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
/*
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text
*/

//@Root(strict = false)
@Serializable
data class Class(
   // @Attribute(name = "id", required = false)
    @SerialName(value = "type_id"/*, alternate = ["id"]*/)
    var typeId: String = "",

    //@Text
    @SerialName(value = "type_name"/*, alternate = ["name"]*/)
    var typeName: String = "",

    @SerialName("type_flag")
    var typeFlag: String = "",

    @SerialName("filters")
    var filters: List<Filter> = emptyList(),

    @SerialName("land")
    var land: Int = 0,

    @SerialName("circle")
    var circle: Int = 0,

    @SerialName("ratio")
    var ratio: Float = 0f,

    var filter: Boolean  = false,
    var activated: Boolean = false
) /*: Parcelable*/ {

    companion object {
        fun objectFrom(json: String): Class {
            return Json.decodeFromString(json)
        }
    }

    fun toggleFilter(): Boolean {
        filter = !filter
        return filter
    }

    fun isHome(): Boolean {
        return "home" == typeId
    }

    fun trans() {
        if (Trans.pass()) return
        typeName = Trans.s2t(typeName.orEmpty())
    }

    fun getStyle(): Style? {
        return Style.get(land, circle, ratio)
    }

    fun getExtend(change: Boolean): HashMap<String, String> {
        val extend = hashMapOf<String, String>()
        filters.forEach { filter ->
            filter.init.let {
                extend[filter.key] = if (change) filter.setActivated(it) else it
            }
        }
        return extend
    }

    override fun hashCode(): Int {
        return typeId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Class

        if (typeId != other.typeId) return false
        if (typeName != other.typeName) return false
        if (typeFlag != other.typeFlag) return false
        if (filters != other.filters) return false
        if (land != other.land) return false
        if (circle != other.circle) return false
        if (ratio != other.ratio) return false
        if (filter != other.filter) return false
        if (activated != other.activated) return false

        return true
    }

    override fun toString(): String {
        return "Class(typeName='$typeName', typeId='$typeId')"
    }


}
