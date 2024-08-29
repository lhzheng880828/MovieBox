package com.calvin.box.movie.bean

import io.github.aakira.napier.Napier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class Result(
    @SerialName("class")
    var types: List<Class> = mutableListOf(),
    @SerialName("list")
    var list: List<Vod> = mutableListOf(),
    @SerialName("filters")
    val filters: LinkedHashMap<String, List<Filter>> = LinkedHashMap(),
    @SerialName("url")
    var url: Url = Url(),
    @SerialName("msg")
    var msg: String = "",
    val subs: List<Sub> = mutableListOf(),
    var header: String? = null,
    var playUrl: String = "",
    val jxFrom: String = "",
    var flag: String = "",
    val danmaku: String = "",
    val format: String = "",
    var click: String = "",
    val js: String = "",
    var key: String = "",
    val pagecount: Int = 0,
    var parse: Int = 0,
    val code: Int = 0,
    val jx: Int = 0,
    val drm: Drm? = null
) {
    companion object {
        val json:Json = Json { ignoreUnknownKeys = true }

        fun fromJson(str: String?): Result {
            if(str.isNullOrEmpty()){
                return empty()
            }
            return try {
                json.decodeFromString(str)
            } catch (e: Exception) {
                Napier.e { "#Result#fromJson method call error: ${e.message}" }
                empty()
            }
        }

        fun fromXml(str: String): Result {
            return try {
                // Use appropriate XML parsing logic here for Kotlin Multiplatform
                // Example: XML parsing logic using kotlinx-serialization
                throw NotImplementedError("XML parsing logic not implemented")
            } catch (e: Exception) {
                e.printStackTrace()
                empty()
            }
        }

        fun fromType(type: Int, str: String): Result {
            return if (type == 0) {
                fromXml(str)
            } else {
                fromJson(str)
            }
        }

        fun fromObject(obj: JsonObject): Result {
            return  json.decodeFromJsonElement(obj)
        }

        private fun empty(): Result {
            return Result(
                types = emptyList(),
                list = emptyList(),
                filters = LinkedHashMap(),
                url = Url.create(),
                msg = "",
                subs = emptyList(),
                header = null,
                playUrl = "",
                jxFrom = "",
                flag = "",
                danmaku = "",
                format = "",
                click = "",
                js = "",
                key = "",
                pagecount = 0,
                parse = 0,
                code = 0,
                jx = 0,
                drm = null
            )
        }

        fun folder(item: Vod): Result {
            val type = Class().apply {
                typeFlag = "1"
                typeId = item.vodId.toString()
                typeName = item.vodName
            }
            return Result(
                types = listOf(type),
                list = emptyList(),
                filters = LinkedHashMap(),
                url = Url.create(),
                msg = "",
                subs = emptyList(),
                header = null,
                playUrl = "",
                jxFrom = "",
                flag = "",
                danmaku = "",
                format = "",
                click = "",
                js = "",
                key = "",
                pagecount = 0,
                parse = 0,
                code = 0,
                jx = 0,
                drm = null
            )
        }

        fun type(json: String): Result {
            val result = Result(
                types = listOf(Class.objectFrom(json)),
                list = emptyList(),
                filters = LinkedHashMap(),
                url = Url.create(),
                msg = "",
                subs = emptyList(),
                header = null,
                playUrl = "",
                jxFrom = "",
                flag = "",
                danmaku = "",
                format = "",
                click = "",
                js = "",
                key = "",
                pagecount = 0,
                parse = 0,
                code = 0,
                jx = 0,
                drm = null
            )
            return result.trans()
        }

        fun list(items: List<Vod>): Result {
            return Result(
                types = emptyList(),
                list = items,
                filters = LinkedHashMap(),
                url = Url.create(),
                msg = "",
                subs = emptyList(),
                header = null,
                playUrl = "",
                jxFrom = "",
                flag = "",
                danmaku = "",
                format = "",
                click = "",
                js = "",
                key = "",
                pagecount = 0,
                parse = 0,
                code = 0,
                jx = 0,
                drm = null
            )
        }

        fun vod(item: Vod): Result {
            return list(listOf(item))
        }
    }




    fun setUrl(url: String) {
        this.url = this.url.replace(url)
    }

    fun getConvertMsg(): String {
        return if (msg.isEmpty() || code != 0) "" else msg
    }





    fun getParse(def: Int): Int {
        return parse
    }


    fun hasMsg(): Boolean {
        return getConvertMsg().isNotEmpty()
    }

    fun getRealUrl(): String {
        return playUrl + url.v()
    }

    fun getHeaders(): Map<String, String>? {
        if(header == null) return mutableMapOf()
        return com.calvin.box.movie.utils.Json.toMap(header)
    }

    fun getStyle(style: Style): Style {
        return if (list.isEmpty()) Style.rect() else list[0].getStyle(style)
    }

    fun clear(): Result {
         list = emptyList()
        return this
    }

    fun trans(): Result {
        if (Trans.pass()) return this
        types.forEach { it.trans() }
        list.forEach { it.trans() }
        subs.forEach { it.trans() }
        return this
    }

    override fun toString(): String {
        return json.encodeToString(serializer(), this)
    }
}
