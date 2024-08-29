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
class SuggestTwo {
    @SerialName("data")
    private val data: Data? = null

    companion object {
        val json = Json { ignoreUnknownKeys=true }
        private fun objectFrom(str: String): SuggestTwo {
            return json.decodeFromString(str)
        }

        fun get(str: String): List<String> {
            return try {
                objectFrom(str).getGroupData().map { it.action.actionArgs.searchKeyword.strVal }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private fun getGroupData(): List<GroupData> {
        return data?.searchData?.vecGroupData?.getOrNull(0)?.groupData ?: emptyList()
    }

    @Serializable
    data class Data(
        @SerialName("search_data")
        val searchData: SearchData? = null
    )

    @Serializable
    data class SearchData(
        @SerialName("vecGroupData")
        val vecGroupData: List<VecGroupData> = emptyList()
    )

    @Serializable
    data class VecGroupData(
        @SerialName("group_data")
        val groupData: List<GroupData> = emptyList()
    )

    @Serializable
    data class GroupData(
        @SerialName("action")
        val action: Action = Action()
    )

    @Serializable
    data class Action(
        @SerialName("actionArgs")
        val actionArgs: ActionArgs = ActionArgs()
    )

    @Serializable
    data class ActionArgs(
        @SerialName("search_keyword")
        val searchKeyword: SearchKeyword = SearchKeyword()
    )

    @Serializable
    data class SearchKeyword(
        @SerialName("strVal")
        val strVal: String = ""
    )
}