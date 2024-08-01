package com.calvin.box.movie.nano

import com.calvin.box.movie.api.config.LiveConfig.Companion.getResp
import com.calvin.box.movie.bean.Device
import com.calvin.box.movie.getSpiderLoader
import com.calvin.box.movie.nano.process.Action
import com.calvin.box.movie.nano.process.Cache
import com.calvin.box.movie.nano.process.Local
import com.calvin.box.movie.nano.process.Media
import com.calvin.box.movie.nano.process.Process
import com.calvin.box.movie.utils.getAndroidId
import com.calvin.box.movie.utils.getDeviceName
import com.github.catvod.utils.Asset
import com.github.catvod.utils.Util
import com.google.common.net.HttpHeaders
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response.IStatus
import io.github.aakira.napier.Napier
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.util.Locale
import java.util.regex.Pattern


class Nano(port: Int) : NanoHTTPD(port) {
    private var process: MutableList<Process> = mutableListOf()

    init {
        addProcess()
    }

    private fun addProcess() {
        process = ArrayList()
        process.add(Action())
        process.add(Cache())
        process.add(Local())
        process.add(Media())
    }

    override fun serve(session: IHTTPSession): Response {
        var url = session.uri.trim { it <= ' ' }
        val files: Map<String, String> = HashMap()
        if (session.method == Method.POST) parse(session, files)
        Napier.d { "#serve request url: $url" }
        if (url.contains("?")) url = url.substring(0, url.indexOf('?'))
        if (url.startsWith("/go")) return go()
        if (url.startsWith("/proxy")) return runBlocking {  proxy(session) }
        if (url.startsWith("/tvbus")) return success(getResp())
        //type = 1, mobile
        val device = Device(uuid = getAndroidId(), name =  getDeviceName(), ip = Server.get().address, type = 1 )
        val json = Json { ignoreUnknownKeys = true }
        if (url.startsWith("/device")) return success(json.encodeToString(device))
        if (url.startsWith("/license")) return success(
            String(
                Util.decode(
                    url.substring(9),
                    Util.URL_SAFE
                )
            )
        )
        for (process in process) if (process.isRequest(session, url)) return process.doResponse(
            session,
            url,
            files
        )
        return getAssets(url.substring(1))
    }


    private fun parse(session: IHTTPSession, files: Map<String, String>) {
        val ct = session.headers["content-type"]
        if (ct != null && ct.lowercase(Locale.getDefault())
                .contains("multipart/form-data") && !ct.lowercase(
                Locale.getDefault()
            ).contains("charset=")
        ) {
            val matcher = Pattern.compile(
                "[ |\t]*(boundary[ |\t]*=[ |\t]*['|\"]?[^\"^'^;^,]*['|\"]?)",
                Pattern.CASE_INSENSITIVE
            ).matcher(ct)
            val boundary = if (matcher.find()) matcher.group(1) else null
            if (boundary != null) session.headers["content-type"] =
                "multipart/form-data; charset=utf-8; $boundary"
        }
        try {
            session.parseBody(files)
        } catch (ignored: Exception) {
        }
    }

    private fun go(): Response {
        Go.start()
        return success()
    }

    private suspend fun proxy(session: IHTTPSession): Response {
        try {
            val params = session.parms
            params.putAll(session.headers)
            val rs: Array<Any> = getSpiderLoader().proxyLocal(params) ?: return error("proxy is empty")

            for (it in rs){
                Napier.d { "#proxy loop: $it"  }
            }

            if (rs[0] is Response) return rs[0] as Response
            val response = newChunkedResponse(
                Response.Status.lookup(
                    (rs[0] as Int)
                ), rs[1] as String, rs[2] as InputStream
            )
            if (rs.size > 3) for ((key, value) in (rs[3] as Map<String, String>)) response.addHeader(
                key, value
            )
            return response
        } catch (e: Exception) {
            return error(e.message)
        }
    }

    private fun getAssets(path: String): Response {
        var path = path
        try {
            if (path.isEmpty()) path = "index.html"
            val `is` = Asset.open(path)
            return newFixedLengthResponse(
                Response.Status.OK,
                getMimeTypeForFile(path),
                `is`,
                `is`.available().toLong()
            )
        } catch (e: Exception) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_HTML, null)
        }
    }

    override fun stop() {
        super.stop()
        Go.stop()
    }

    companion object{
        fun success(text: String? = "OK"): Response {
            return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, text)
        }

        fun error(text: String?): Response {
            return error(Response.Status.INTERNAL_ERROR, text)
        }

        fun error(status: IStatus?, text: String?): Response {
            return newFixedLengthResponse(status, MIME_PLAINTEXT, text)
        }

        fun redirect(url: String?, headers: Map<String?, String?>): Response {
            val response = newFixedLengthResponse(Response.Status.REDIRECT, MIME_HTML, "")
            for ((key, value) in headers) response.addHeader(key, value)
            response.addHeader(HttpHeaders.LOCATION, url)
            return response
        }
    }

}
