package com.calvin.box.movie.bean

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/8/29
 */

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class Suggest {
    @SerialName("data")
    private val data: List<Data> = emptyList()

    companion object {
        private fun objectFrom(str: String): Suggest {
            return Json.decodeFromString(str)
        }

        fun get(str: String): List<String> {
            return try {
                objectFrom(str).data.map { it.name }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    @Serializable
    data class Data(
        @SerialName("name")
        val name: String = ""
    )
}