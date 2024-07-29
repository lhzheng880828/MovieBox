package com.calvin.box.movie.utils

import com.calvin.box.movie.server.Server
import io.github.aakira.napier.Napier
import io.ktor.http.*
import kotlin.text.*

object UrlUtil {

    fun uri(url: String): Url {
        return Url(url.trim().replace("\\", ""))
    }

    fun scheme(url: String?): String {
        return url?.let { scheme(Url(it)) } ?: ""
    }

    fun scheme(url: Url): String {
        return url.protocol.name.lowercase().trim()
    }

    fun host(url: String?): String {
        return url?.let { host(Url(it)) } ?: ""
    }

    fun host(url: Url): String {
        return url.host.lowercase().trim()
    }

    fun path(url: Url): String {
        return url.encodedPath.trim()
    }

    fun resolve(baseUri: String, referenceUri: String): String {
        val resolved = URLBuilder(baseUri).apply {
            takeFrom(referenceUri)
        }.buildString()
        Napier.d{"resolved url: $resolved"}
        return resolved
    }

    fun convert(url: String): String {
        val scheme = scheme(url)
        return when {
            scheme == "clan" -> convert(fixUrl(url))
            scheme == "local" -> url.replace("local://", Server.getAddress(""))
            scheme == "assets" -> url.replace("assets://", Server.getAddress(""))
            scheme == "file" -> url.replace("file://", Server.getAddress("file/"))
            scheme == "proxy" -> url.replace("proxy://", Server.getAddress("proxy?"))
            else -> url
        }
    }

    fun fixUrl(url: String): String {
        var fixedUrl = url
        if (fixedUrl.contains("/localhost/")) fixedUrl = fixedUrl.replace("/localhost/", "/")
        if (fixedUrl.startsWith("clan")) fixedUrl = fixedUrl.replace("clan", "file")
        return fixedUrl
    }

    fun fixHeader(key: String): String {
        return when {
            HttpHeaders.UserAgent.equals(key, ignoreCase = true) -> HttpHeaders.UserAgent
            HttpHeaders.Referrer.equals(key, ignoreCase = true) -> HttpHeaders.Referrer
            HttpHeaders.Cookie.equals(key, ignoreCase = true) -> HttpHeaders.Cookie
            else -> key
        }
    }

    fun fixDownloadUrl(url: String?): String {
        if (url.isNullOrEmpty()) return ""
        val uri = uri(url)
        if (!uri.toString().startsWith("http://127.0.0.1:")) return uri.toString()
        val download = uri.parameters["url"]
        return download ?: uri.toString()
    }
}

