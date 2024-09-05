package com.calvin.box.movie.utils

import com.calvin.box.movie.saveFile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/9/5
 */
interface DownloadCallback {
    fun onProgress(progress: Int)
    fun onSuccess(filePath: String)
    fun onError(exception: Throwable)
}
class FileDownloader(
    private val client: HttpClient,
    val callback: DownloadCallback
) {

    suspend fun downloadFile(url: String, savePath: String) {
        try {
            val response: HttpResponse =
                withContext(Dispatchers.IO){
                    client.get(url)
                }
            val totalSize = response.contentLength() ?: -1L
            val channel: ByteReadChannel = response.body()
            saveFile(channel, savePath, totalSize)
            callback.onSuccess(savePath)
        } catch (e: Exception) {
            callback.onError(e)
        }
    }

}
