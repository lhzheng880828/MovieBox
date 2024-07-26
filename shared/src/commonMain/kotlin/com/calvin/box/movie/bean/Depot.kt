package com.calvin.box.movie.bean

import com.calvin.box.movie.utils.UrlUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Depot(
    val url: String = "",
    val name: String = ""
) {

    companion object {
        fun arrayFrom(str: String): List<Depot> {
            return try {
                Json.decodeFromString(str)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun getDepotUrl(): String {
        return if (url.isEmpty()) "" else UrlUtil.fixUrl(url)
    }

    fun getDepotName(): String {
        return name.ifEmpty { getDepotUrl() }
    }
}

