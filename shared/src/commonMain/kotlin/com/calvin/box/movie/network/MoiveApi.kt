/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.calvin.box.movie.network

import com.calvin.box.movie.bean.Hot
import com.calvin.box.movie.bean.Suggest
import com.calvin.box.movie.bean.SuggestTwo
import com.calvin.box.movie.model.Fruittie
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodeURLParameter
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.coroutines.cancellation.CancellationException

interface MoiveApi {
    suspend fun getData(pageNumber: Int = 0): MoivesResponse
    suspend fun getHotword(): Hot
    suspend fun getSuggest(keyword: String): Flow<List<String>>
}

class MoiveNetworkApi(private val client: HttpClient, private val apiUrl: String) : MoiveApi {

    override suspend fun getData(pageNumber: Int): MoivesResponse {
        val url = apiUrl + "api/$pageNumber"
        return try {
            client.get(url).body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()

            MoivesResponse(emptyList(), 0, 0)
        }
    }

    override suspend fun getHotword(): Hot {
        return try {
            client.get("https://api.web.360kan.com/v1/rank?cat=1") {
                header("Referer", "https://www.360kan.com/rank/general")
            }.body()
        } catch (e: Exception) {
            //if (e is CancellationException) throw e
            e.printStackTrace()
            val empty = Hot()
            empty.setData(emptyList())
            return empty
        }
    }

    override suspend fun getSuggest(keyword: String): Flow<List<String>> = flow {
        val combinedItems = mutableListOf<String>()
        val encodedText = keyword.encodeURLParameter()

        coroutineScope {
            val suggestTwoDeferred = async {
                try {
                    val suggestTwoUrl =
                        "https://tv.aiseet.atianqi.com/i-tvbin/qtv_video/search/get_search_smart_box?format=json&page_num=0&page_size=20&key=$encodedText"
                    val suggestTwoResponse: HttpResponse = client.get(suggestTwoUrl)
                    val twoResponseText = suggestTwoResponse.bodyAsText()
                    Napier.d { "twoResponseText: $twoResponseText" }
                    SuggestTwo.get(twoResponseText)
                } catch (e: Exception) {
                    Napier.e { "getSuggestTwo error: ${e.message}" }
                    emptyList()
                }
            }

            val suggestDeferred = async {
                try {
                    val suggestUrl = "https://suggest.video.iqiyi.com/?if=mobile&key=$encodedText"
                    val suggestResponse: HttpResponse = client.get(suggestUrl)
                    val oneResponseText = suggestResponse.bodyAsText()
                    Napier.d { "oneResponseText: $oneResponseText" }
                    Suggest.get(oneResponseText)
                } catch (e: Exception) {
                    Napier.e { "getSuggest error: ${e.message}" }
                    emptyList()
                }
            }
            val data1 = suggestTwoDeferred.await()
            emit(data1)
            combinedItems.addAll(data1)
            val data2 = suggestDeferred.await()
            emit(data2)
            combinedItems.addAll(data2)
        }

        Napier.d { "combinedItems size: ${combinedItems.size}" }
        val lastCombinedItems = combinedItems.distinct().take(20)
        emit(lastCombinedItems)
    }
}


@Serializable
data class MoivesResponse(
    @SerialName("feed")
    val feed: List<Fruittie>,
    @SerialName("totalPages")
    val totalPages: Int,
    @SerialName("currentPage")
    val currentPage: Int,
)