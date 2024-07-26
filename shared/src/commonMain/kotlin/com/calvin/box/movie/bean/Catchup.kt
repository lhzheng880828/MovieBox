package com.calvin.box.movie.bean

import kotlinx.serialization.SerialName
import  com.calvin.box.movie.getPlatform
import kotlinx.serialization.Serializable


@Serializable
data class Catchup(
    @SerialName("type") var type: String = "",
    @SerialName("days") private var days: String = "",
    @SerialName("regex") private var regex: String = "",
    @SerialName("source") var source: String = ""
) {

    companion object {
        fun PLTV(): Catchup {
            return Catchup().apply {
                days = "7"
                type = "append"
                regex = "/PLTV/"
                source = "?playseek=\${(b)yyyyMMddHHmmss}-\${(e)yyyyMMddHHmmss}"
            }
        }

        fun create(): Catchup {
            return Catchup()
        }

        fun decide(major: Catchup, minor: Catchup): Catchup {
            if (!major.isEmpty()) return major
            if (!minor.isEmpty()) return minor
            return Catchup()
        }
    }

    fun match(url: String): Boolean {
        getPlatform()
        return url.contains(regex) || getPlatform().getPlatformRegex(regex).matches(url)
    }

    fun isEmpty(): Boolean {
        return source.isEmpty()
    }

    private fun isAppend(): Boolean {
        return type == "append"
    }

    private fun isDefault(): Boolean {
        return  type == "default"
    }

    private fun format(url: String, result: String): String {
        var formattedUrl = url
        val query = getPlatform().getUriQuery(url)
        if (query.isNullOrEmpty()) {
            formattedUrl = result.replace("?", "&")
        }
        if (url.contains("/PLTV/")) {
            formattedUrl = url.replace("/PLTV/", "/TVOD/")
        }
        return formattedUrl + result
    }

    fun format(url: String, data: EpgData): String {
        var result = source
        if (data.isInRange()) {
            return url
        }
        val regex = getPlatform().getPlatformRegex("(\\$\\{[^}]*\\})")
        var matchResult = regex.find(result)
        while (matchResult != null) {
            val placeholder = matchResult.groupValues[1]
            result = result.replace(placeholder, data.format(placeholder))
            matchResult = regex.find(result, matchResult.range.last + 1)
        }
        return if (isDefault()) result else format(url, result)
    }
}
