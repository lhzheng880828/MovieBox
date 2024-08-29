package com.calvin.box.movie.utils

import com.calvin.box.movie.App.execute
import com.github.catvod.net.HostOkHttp
import com.github.catvod.utils.Path
import com.google.common.net.HttpHeaders
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AndroidDownload(
    private val url: String,
    private val file: File,
    private val callback: Callback?
) {
    fun start() {
        if (url.startsWith("file")) return
        if (callback == null) doInBackground()
        else execute(::doInBackground)
    }

    private fun doInBackground() {
        try {
            Path.create(file)
            val response = HostOkHttp.newCall(url).execute()
            download(
                response.body!!.byteStream(), response.header(HttpHeaders.CONTENT_LENGTH, "1")!!
                    .toDouble()
            )
            callback?.success(
                file
            )
        } catch (e: Exception) {
            callback?.error(e.message)
        }
    }

    @Throws(Exception::class)
    private fun download(`is`: InputStream, length: Double) {
        val os = FileOutputStream(file)
        BufferedInputStream(`is`).use { input ->
            val buffer = ByteArray(4096)
            var readBytes: Int
            var totalBytes: Long = 0
            while ((input.read(buffer).also { readBytes = it }) != -1) {
                totalBytes += readBytes.toLong()
                os.write(buffer, 0, readBytes)
                val progress = (totalBytes / length * 100.0).toInt()
                callback?.progress(progress)
            }
        }
    }

    interface Callback {
        fun progress(progress: Int)

        fun error(msg: String?)

        fun success(file: File?)
    }

    companion object {
        @JvmOverloads
        fun create(url: String, file: File, callback: Callback? = null): AndroidDownload {
            return AndroidDownload(url, file, callback)
        }
    }
}
