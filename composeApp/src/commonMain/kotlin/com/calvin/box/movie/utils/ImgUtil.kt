package com.calvin.box.movie.utils

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/7/29
 */
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import io.kamel.core.DataSource
import io.kamel.core.Resource
import io.ktor.http.*
import io.kamel.core.config.*
import io.kamel.image.config.*
import io.kamel.core.utils.URL
import io.kamel.image.KamelImage

object UrlProcessor {
    fun processUrl(url: String): Pair<String, Map<String, String>> {
        var processedUrl = UrlUtil.convert(url)
        if (processedUrl.startsWith("data:")) return processedUrl to emptyMap()

        val headers = mutableMapOf<String, String>()
        var param: String? = null

        when {
            "@Headers=" in processedUrl -> {
                param = processedUrl.split("@Headers=")[1].split("@")[0]
                addHeaders(headers, param)
            }
            "@Cookie=" in processedUrl -> {
                param = processedUrl.split("@Cookie=")[1].split("@")[0]
                headers[HttpHeaders.Cookie] = param
            }
            "@Referer=" in processedUrl -> {
                param = processedUrl.split("@Referer=")[1].split("@")[0]
                headers[HttpHeaders.Referrer] = param
            }
            "@User-Agent=" in processedUrl -> {
                param = processedUrl.split("@User-Agent=")[1].split("@")[0]
                headers[HttpHeaders.UserAgent] = param
            }
        }

        processedUrl = param?.let { processedUrl.split("@")[0] } ?: processedUrl

        return processedUrl to headers
    }

    private fun addHeaders(headers: MutableMap<String, String>, headersString: String) {
        headersString.split("&").forEach { header ->
            val parts = header.split("=")
            if (parts.size == 2) {
                headers[parts[0]] = parts[1]
            }
        }
    }
}

