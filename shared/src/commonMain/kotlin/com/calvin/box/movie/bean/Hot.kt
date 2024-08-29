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
class Hot {
    @SerialName("data")
    private var data: List<Data> = emptyList()

    companion object {
        val json = Json { ignoreUnknownKeys=true }
        private fun objectFrom(str: String): Hot {
            return json.decodeFromString(str)
        }

        fun get(str: String): List<String> {
            return try {
                val items = objectFrom(str).data.map { it.title }
                items
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun getData(): List<Data> = data
    fun setData(dataList: List<Data>){
        data = dataList
    }

    @Serializable
    data class Data(
        @SerialName("title")
        val title: String
    )
}