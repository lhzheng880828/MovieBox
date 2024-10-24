package com.calvin.box.movie.bean

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable
data class Channel(
    @SerialName("urls") var urls: MutableList<String> = mutableListOf(),
    @SerialName("tvgName") var tvgName: String = "",
    @SerialName("number") var number: String = "",
    @SerialName("logo") var logo: String = "",
    @SerialName("epg") var epg: String =  "",
    @SerialName("name") var name: String = "",
    @SerialName("ua") var ua: String = "",
    @SerialName("click") var click: String = "",
    @SerialName("format") var format: String? = null,
    @SerialName("origin") var origin: String = "",
    @SerialName("referer") var referer: String =  "",
    @SerialName("catchup") var catchup: Catchup = Catchup(),
    @SerialName("header") var header: JsonElement? = null,
    @SerialName("playerType") var playerType: Int? = null,
    @SerialName("parse") var parse: Int? = null,
    @SerialName("drm") var drm: Drm? = null,
    var selected: Boolean = false,
    var group: Group = Group(),
    var url: String =  "",
    var msg: String? = null,
    var data: Epg? = null,
    var line: Int = 0
) {
    companion object {
        fun create(number: Int): Channel {
            return Channel().setNumber(number)
        }

        fun create(name: String): Channel {
            return Channel(name = name)
        }

        fun create(channel: Channel?): Channel {
            return Channel().copy(channel!!)
        }

        fun error(msg: String): Channel {
            val result = Channel()
            result.msg = msg
            return result
        }
    }


  /*  fun getLineVisible(): Int {
        return if (isOnly()) View.GONE else View.VISIBLE
    }

    fun loadLogo(view: ImageView) {
        ImgUtil.loadLive(logo, view)
    }*/

    fun addUrls(urls: List<String>) {
        this.urls.addAll(urls)
    }

    fun nextLine() {
        line = if (line < (urls.size ?: (0 - 1))) line + 1 else 0
    }

    fun prevLine() {
        line = if (line > 0) line - 1 else (urls.size ?: 0) - 1
    }

    fun getCurrent(): String {
        return urls.getOrNull(line) ?: ""
    }

    fun isOnly(): Boolean {
        return urls.size == 1
    }

    fun isLast(): Boolean {
        return urls.isEmpty() ?: true || line == (urls.size ?: 0) - 1
    }

    fun hasCatchup(): Boolean {
        if (catchup.isEmpty() && getCurrent().contains("/PLTV/")) {
            catchup = Catchup.PLTV()
        }
        return catchup.match(getCurrent()) ?: false
    }

    fun getLineText(): String {
        if ((urls.size ?: 0) <= 1) return ""
        return if (getCurrent().contains("$")) getCurrent().split("\\$")[1] else "live line ${line+1}" /*ResUtil.getString(R.string.live_line, line + 1)*/
    }

    fun setNumber(number: Int): Channel {
        this.number =  "$number".padStart(3, '0')
        return this
    }

    fun group(group: Group): Channel {
        this.group = group
        return this
    }


    fun live(live: Live) {
        if (live.ua.isNotEmpty() && ua.isEmpty()) ua = live.ua
        if (live.header != null && header == null) header = live.header
        if (live.click.isNotEmpty() && click.isNullOrEmpty()) click = live.click
        if (live.origin.isNotEmpty() && origin.isEmpty()) origin = live.origin
         catchup = live.catchup
        if (live.referer.isNotEmpty() && referer.isEmpty()) referer = live.referer
        if (live.playerType != -1 && (playerType ?: -1) == -1) playerType = live.playerType
        if (!epg.startsWith("http") ?: false && live.epg.contains("{")) epg = live.epg.replace("{name}", tvgName ?: "").replace("{epg}", epg ?: "")
        if (!logo.startsWith("http") ?: false && live.logo.contains("{")) logo = live.logo.replace("{name}", tvgName ?: "").replace("{logo}", logo ?: "")
    }

    fun setLine(line: String) {
        this.line = urls.indexOf(line) ?: 0
    }

    fun getHeaders(): Map<String, String> {
        if(header == null) return mutableMapOf()
        val headers = com.calvin.box.movie.utils.Json.toMap(header!!).toMutableMap()
        if (ua.isNotEmpty()) headers[HttpHeaders.UserAgent] = ua
        if (origin.isNotEmpty()) headers[HttpHeaders.Origin] = origin
        if (referer.isNotEmpty()) headers[HttpHeaders.Referrer] = referer
        return headers
    }

    fun copy(item: Channel): Channel {
        playerType = item.playerType
        catchup = item.catchup
        referer = item.referer
        tvgName = item.tvgName
        header = item.header
        number = item.number
        origin = item.origin
        format = item.format
        parse = item.parse
        click = item.click
        logo = item.logo
        name = item.name
        urls = item.urls
        data = item.data
        drm = item.drm
        epg = item.epg
        ua = item.ua
        return this
    }

    fun result(): Result {
        val result = Result()
        result.click = click
        result.url = Url.create().add(url)
        val json = Json { ignoreUnknownKeys=true }
        result.header = json.encodeToString(getHeaders())
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Channel) return false
        return name == other.name || (number.isNotEmpty() && number == other.number)
    }

    override fun hashCode(): Int {
        var result = urls.hashCode() ?: 0
        result = 31 * result + (tvgName.hashCode() ?: 0)
        result = 31 * result + (number.hashCode() ?: 0)
        result = 31 * result + (logo.hashCode() ?: 0)
        result = 31 * result + (epg.hashCode() ?: 0)
        result = 31 * result + (name.hashCode() ?: 0)
        result = 31 * result + (ua.hashCode() ?: 0)
        result = 31 * result + (click.hashCode() ?: 0)
        result = 31 * result + (format?.hashCode() ?: 0)
        result = 31 * result + (origin.hashCode() ?: 0)
        result = 31 * result + (referer.hashCode() ?: 0)
        result = 31 * result + (catchup.hashCode() ?: 0)
        result = 31 * result + (header?.hashCode() ?: 0)
        result = 31 * result + (playerType?: 0)
        result = 31 * result + (parse?: 0)
        result = 31 * result + (drm?.hashCode() ?: 0)
        result = 31 * result + selected.hashCode()
        result = 31 * result + (group?.hashCode() ?: 0)
        result = 31 * result + (url.hashCode() ?: 0)
        result = 31 * result + (msg?.hashCode() ?: 0)
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + line
        return result
    }
}
