package com.calvin.box.movie.bean

import com.calvin.box.movie.utils.UrlUtil
import io.ktor.utils.io.core.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

data class Parse(
    @SerialName("name")
    var name: String = "",
    @SerialName("type")
    var type: Int = 0,
    @SerialName("url")
    var url: String = "",
    @SerialName("ext")
    var ext: Ext = Ext(header = null),
    var isActivated: Boolean = false,
    var click: String? = null
) {

    fun getConvertUrl():String{
        return UrlUtil.convert(url)
    }

    fun setActivated(item: Parse) {
        this.isActivated = (item == this)
    }

    val headers: Map<String, String>?
        get() = ext.header?.let { com.calvin.box.movie.utils.Json.toMap(it).toMutableMap() }

    fun setHeader(header: JsonElement) {
        if (ext.header == null) ext.header = header
    }

    val isEmpty: Boolean
        get() = type == 0 && url.isEmpty()

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj !is Parse) return false
        return name == obj.name
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun extUrl(): String {
        val index = url.indexOf("?")
        if (ext.isEmpty || index == -1) return url
        return (url.substring(0, index + 1) + "cat_ext=" +/* Util.base64(
            ext.toString(),
            Util.URL_SAFE
        )).toString()*/Base64.encode(ext.toString().toByteArray()) + "&" + url.substring(index + 1))
    }

    fun mixMap(): Map<String, String> {
        val map: Map<String, String> = hashMapOf()
        map.plus(pair = Pair("type", type.toString()))
        map.plus(pair = Pair("ext", ext.toString()))
        map.plus(pair = Pair("url", url))
        return map
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + ext.hashCode()
        result = 31 * result + isActivated.hashCode()
        result = 31 * result + (click?.hashCode() ?: 0)
        return result
    }


    companion object {
        fun objectFrom(element: JsonElement): Parse {
            return Json.decodeFromJsonElement(element)
        }

        fun get(name: String): Parse {
            val parse = Parse(name = name)
            return parse
        }

        fun get(type: Int, url: String): Parse {
            val parse = Parse(url = url, type = type)
            parse.type = type
            parse.url = url
            return parse
        }

        fun god(): Parse {
            val parse = Parse(name ="parse god" /*ResUtil.getString(R.string.parse_god)*/, type = 4)
            return parse
        }
    }
}

data class Ext (
    @SerialName("flag")
     var flag: List<String> = emptyList(),
    @SerialName("header")
 var header: JsonElement?
){
    val isEmpty: Boolean
        get() = header == null && flag.isEmpty()


}
