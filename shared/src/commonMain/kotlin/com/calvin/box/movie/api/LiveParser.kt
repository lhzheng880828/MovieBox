package com.calvin.box.movie.api

import com.calvin.box.movie.AesDecoder
import com.calvin.box.movie.PlatformDecoder
import com.calvin.box.movie.bean.*
import com.calvin.box.movie.player.Players
import com.calvin.box.movie.utils.UrlUtil
import com.calvin.box.movie.utils.Json
import com.calvin.box.movie.getPlatform
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class LiveParser {

    companion object {
        private val CATCHUP_SOURCE = getPlatform().getPlatformRegex(".*catchup-source=\"(.?|.+?)\".*")
        private val CATCHUP = getPlatform().getPlatformRegex(".*catchup=\"(.?|.+?)\".*")
        private val TVG_NAME = getPlatform().getPlatformRegex(".*tvg-name=\"(.?|.+?)\".*")
        private val TVG_LOGO = getPlatform().getPlatformRegex(".*tvg-logo=\"(.?|.+?)\".*")
        private val TVG_URL = getPlatform().getPlatformRegex(".*x-tvg-url=\"(.?|.+?)\".*")
        private val GROUP = getPlatform().getPlatformRegex(".*group-title=\"(.?|.+?)\".*")
        private val NAME = getPlatform().getPlatformRegex(".*,(.+?)$")

        private fun extract(line: String, regex: Regex): String {
            val matchResult = regex.matchEntire(line.trim())
            return matchResult?.groupValues?.get(1) ?: ""
        }

        suspend fun start(live: Live) {
            if (live.groups.isNotEmpty()) return
            val text = getText(live)
            when (live.type) {
                0 -> text(live, text)
                1 -> json(live, text)
                2 -> proxy(live, text)
            }
        }

        fun text(live: Live, text: String) {
            var number = 0
            if (live.groups.isNotEmpty()) return
            val modifiedText = text.replace("\r\n", "\n")
            if (modifiedText.contains("#EXTM3U")) m3u(live, modifiedText)
            else txt(live, modifiedText)
            for (group in live.groups) {
                for (channel in group.channels) {
                    channel.number = (++number).toString()
                    channel.live(live)
                }
            }
        }

        private  fun json(live: Live, text: String) {
            live.groups.addAll(Group.arrayFrom(text))
            for (group in live.groups) {
                for (channel in group.channels) {
                    channel.live(live)
                }
            }
        }

        private fun m3u(live: Live, text: String) {
            val setting = Setting.create()
            val catchup = Catchup.create()
            var channel = Channel.create("")
            for (line in text.split("\n")) {
               // if (coroutineContext[Job]?.isCancelled == true) break
                when {
                    setting.find(line) -> setting.check(line)
                    line.startsWith("#EXTM3U") -> {
                        catchup.type = extract(line, CATCHUP)
                        catchup.source = extract(line, CATCHUP_SOURCE)
                        if (live.epg.isEmpty()) live.epg = extract(line, TVG_URL)
                    }
                    line.startsWith("#EXTINF:") -> {
                        val group = live.find(Group.create(extract(line, GROUP), live.pass))
                        channel = group.find(Channel.create(extract(line, NAME)))
                        channel.tvgName = extract(line, TVG_NAME)
                        channel.logo = extract(line, TVG_LOGO)
                        val unknown = Catchup.create().apply {
                            type = extract(line, CATCHUP)
                            source = extract(line, CATCHUP_SOURCE)
                        }
                        channel.catchup = Catchup.decide(unknown, catchup)
                    }
                    !line.startsWith("#") && line.contains("://") -> {
                        val split = line.split("|")
                        if (split.size > 1) setting.headers(split.drop(1).toTypedArray())
                        channel.urls.add(split[0])
                        setting.copy(channel).clear()
                    }
                }
            }
        }

        private  fun txt(live: Live, text: String) {
            val setting = Setting.create()
            for (line in text.split("\n")) {
               // if (coroutineContext[Job]?.isCancelled == true) break
                val split = line.split(",")
                val index = line.indexOf(",") + 1
                if (setting.find(line)) setting.check(line)
                if (line.contains("#genre#")) setting.clear()
                if (line.contains("#genre#")) live.groups.add(Group.create(split[0], live.pass))
                if (split.size > 1 && live.groups.isEmpty()) live.groups.add(Group.create())
                if (split.size > 1 && split[1].contains("://")) {
                    val group = live.groups.last()
                    val channel = group.find(Channel.create(split[0]))
                    val urlList = line.substring(index).split("#")
                    channel.addUrls(urlList)
                    setting.copy(channel)
                }
            }
        }

        private  fun proxy(live: Live, text: String) {
            var number = 0
            for (item in Live.arrayFrom(text)) {
                val group = live.find(Group.create(item.group, live.pass))
                for (channel in item.channels) {
                    channel.number = (++number).toString()
                    channel.live(live)
                    group.add(channel)
                }
            }
        }

        private  fun getText(live: Live): String {
            return getText(live.url, live.getHeaders())
        }

        private val aesDecoder: AesDecoder = PlatformDecoder()

        @OptIn(ExperimentalEncodingApi::class)
        private fun getText(url: String, header: Map<String, String>): String {
            return when {
                url.startsWith("file") -> aesDecoder.loadJsonData(url)// Path.read(url)
                url.startsWith("http") -> aesDecoder.loadJsonData(url)//OkHttp.string(url, header)
                url.startsWith("assets") || url.startsWith("proxy") -> getText(UrlUtil.convert(url), header)
                url.isNotEmpty() && url.length % 4 == 0 -> getText(Base64.decode(url).decodeToString(), header)
                else -> ""
            }
        }
    }

    class Setting {
        var ua: String? = null
        var key: String =  ""
        var type: String? = null
        var click: String? = null
        var format: String? = null
        var origin: String? = null
        var referer: String? = null
        var parse: Int? = null
        var player: Int? = null
        var header: Map<String, String> = mutableMapOf()

        companion object {
            fun create(): Setting {
                return Setting()
            }
            private const val BASE_TYPE_APPLICATION: String = "application"
            const val APPLICATION_MPD: String = "$BASE_TYPE_APPLICATION/dash+xml"
            const val APPLICATION_M3U8: String = "$BASE_TYPE_APPLICATION/x-mpegURL"
        }

        fun find(line: String): Boolean {
            return line.startsWith("ua") || line.startsWith("parse") || line.startsWith("click") || line.startsWith("player") || line.startsWith("header") || line.startsWith("format") || line.startsWith("origin") || line.startsWith("referer") || line.startsWith("#EXTHTTP:") || line.startsWith("#EXTVLCOPT:") || line.startsWith("#KODIPROP:")
        }

        fun check(line: String) {
            when {
                line.startsWith("ua") -> ua(line)
                line.startsWith("parse") -> parse(line)
                line.startsWith("click") -> click(line)
                line.startsWith("player") -> player(line)
                line.startsWith("header") -> header(line)
                line.startsWith("format") -> format(line)
                line.startsWith("origin") -> origin(line)
                line.startsWith("referer") -> referer(line)
                line.startsWith("#EXTHTTP:") -> header(line)
                line.startsWith("#EXTVLCOPT:http-origin") -> origin(line)
                line.startsWith("#EXTVLCOPT:http-user-agent") -> ua(line)
                line.startsWith("#EXTVLCOPT:http-referrer") -> referer(line)
                line.startsWith("#KODIPROP:inputstream.adaptive.license_key") -> key(line)
                line.startsWith("#KODIPROP:inputstream.adaptive.license_type") -> type(line)
                line.startsWith("#KODIPROP:inputstream.adaptive.manifest_type") -> format(line)
                line.startsWith("#KODIPROP:inputstream.adaptive.stream_headers") -> headers(line)
            }
        }

        fun copy(channel: Channel): Setting {
            ua?.let { channel.ua = it }
            parse?.let { channel.parse = it }
            click?.let { channel.click = it }
            format?.let { channel.format = it }
            origin?.let { channel.origin = it }
            referer?.let { channel.referer = it }
            player?.let { channel.playerType = it }
            header.let { channel.header = Json.toObject(it) }
            if (type != null) channel.drm = Drm.create(key, type!!)
            return this
        }

        private fun ua(line: String) {
            ua = when {
                line.contains("user-agent=") -> line.split("(?i)user-agent=")[1].trim().replace("\"", "")
                line.contains("ua=") -> line.split("ua=")[1].trim().replace("\"", "")
                else -> null
            }
        }

        private fun referer(line: String) {
            referer = line.split("(?i)referer=")[1].trim().replace("\"", "")
        }

        private fun parse(line: String) {
            parse = line.split("parse=")[1].trim().toIntOrNull()
        }

        private fun click(line: String) {
            click = line.split("click=")[1].trim()
        }

        private fun player(line: String) {
            player = line.split("player=")[1].trim().toIntOrNull()
        }


        private fun format(line: String) {
            format = when {
                line.startsWith("format=") -> line.split("format=")[1].trim()
                line.contains("manifest_type=") -> line.split("manifest_type=")[1].trim()
                else -> null
            }
            format = when (format) {
                "mpd", "dash" ->/* MimeTypes.APPLICATION_MPD*/APPLICATION_MPD
                "hls" -> /*MimeTypes.APPLICATION_M3U8*/APPLICATION_M3U8
                else -> format
            }
        }

        private fun origin(line: String) {
            origin = line.split("(?i)origin=")[1].trim()
        }

        private fun key(line: String) {
            key = line.split("license_key=")[1].trim()
            if (!key.startsWith("http")) convert()
            player = Players.EXO
        }

        private fun type(line: String) {
            type = line.split("license_type=")[1].trim()
            player = Players.EXO
        }

        private fun header(line: String) {
            header = when {
                line.contains("#EXTHTTP:") -> Json.toMap(Json.parse(line.split("#EXTHTTP:")[1].trim()))
                line.contains("header=") -> Json.toMap(Json.parse(line.split("header=")[1].trim()))
                else -> mutableMapOf()
            }
        }

        private fun headers(line: String) {
            headers(line.split("headers=")[1].trim().split("&").toTypedArray())
        }

        fun headers(params: Array<String>) {
            for (param in params) {
                val a = param.split("=")
                header.plus(Pair(a[0].trim(), a[1].trim().replace("\"", "")))
            }
        }

        private fun convert() {
            key = try {
                ClearKey.objectFrom(key)
                key
            } catch (e: Exception) {
                ClearKey.get(key.replace("\"", "").replace("{", "").replace("}", "")).toString()
            }
        }

        fun clear() {
            ua = null
            key = ""
            type = null
            parse = null
            click = null
            player = null
            header = mutableMapOf()
            format = null
            origin = null
            referer = null
        }
    }
}
