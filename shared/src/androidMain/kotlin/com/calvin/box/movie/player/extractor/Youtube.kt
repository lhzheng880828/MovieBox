package com.calvin.box.movie.player.extractor

import android.net.Uri
import android.util.Base64
import com.calvin.box.movie.bean.Episode
import com.calvin.box.movie.bean.Episode.Companion.create
import com.calvin.box.movie.player.Extractor
import com.github.catvod.net.HostOkHttp
import com.github.kiulian.downloader.YoutubeDownloader
import com.github.kiulian.downloader.downloader.request.RequestPlaylistInfo
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo
import com.github.kiulian.downloader.model.videos.VideoInfo
import com.github.kiulian.downloader.model.videos.formats.AudioFormat
import com.github.kiulian.downloader.model.videos.formats.Format
import com.github.kiulian.downloader.model.videos.formats.VideoFormat
import java.util.Locale
import java.util.concurrent.Callable
import java.util.regex.Pattern

class Youtube : Extractor {
    override fun match(scheme: String?, host: String?): Boolean {
        return host!!.contains("youtube.com") || host.contains("youtu.be")
    }

    @Throws(Exception::class)
    override fun fetch(url: String?): String? {
        if(url == null) return ""
        val matcher = PATTERN_VID.matcher(url)
        if (!matcher.find()) return ""
        val videoId = matcher.group()
        val request = RequestVideoInfo(videoId)
        val info = getDownloader().getVideoInfo(request).data()
        return if (info.details().isLive) info.details().liveUrl() else getMpdWithBase64(info)
    }

    private fun getMpdWithBase64(info: VideoInfo): String {
        val video = StringBuilder()
        val audio = StringBuilder()
        val videoFormats = info.videoFormats()
        val audioFormats = info.audioFormats()
        for (format in videoFormats) video.append(getAdaptationSet(format, getVideoParam(format)))
        for (format in audioFormats) audio.append(getAdaptationSet(format, getAudioParam(format)))
        val mpd = String.format(
            Locale.getDefault(),
            MPD,
            info.details().lengthSeconds(),
            info.details().lengthSeconds(),
            video,
            audio
        )
        return "data:application/dash+xml;base64," + Base64.encodeToString(
            mpd.toByteArray(),
            Base64.DEFAULT
        )
    }

    private fun getVideoParam(format: VideoFormat): String {
        return String.format(
            Locale.getDefault(),
            "height='%d' width='%d' frameRate='%d' maxPlayoutRate='1' startWithSAP='1'",
            format.height(),
            format.width(),
            format.fps()
        )
    }

    private fun getAudioParam(format: AudioFormat): String {
        return String.format(
            Locale.getDefault(),
            "subsegmentAlignment='true' audioSamplingRate='%d'",
            format.audioSampleRate()
        )
    }

    private fun getAdaptationSet(format: Format, param: String): String {
        if (format.initRange() == null || format.indexRange() == null) return ""
        val mimeType = format.mimeType().split(";".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0]
        val contentType = format.mimeType().split("/".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0]
        val iTag = format.itag().id()
        val bitrate = format.bitrate()
        val url = format.url().replace("&", "&amp;")
        val codecs = format.mimeType().split("=".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[1].replace("\"", "")
        val initRange = format.initRange().start.toString() + "-" + format.initRange().end
        val indexRange = format.indexRange().start.toString() + "-" + format.indexRange().end
        return String.format(
            Locale.getDefault(),
            ADAPT,
            contentType,
            iTag,
            bitrate,
            codecs,
            mimeType,
            param,
            url,
            indexRange,
            initRange
        )
    }

    override fun stop() {
        downloader = null
    }

    override fun exit() {
    }

    class Parser(private val url: String) : Callable<List<Episode>> {
        override fun call(): List<Episode> {
            val episodes: MutableList<Episode> = ArrayList()
            val id = Uri.parse(url).getQueryParameter("list")
            val request = RequestPlaylistInfo(id)
            val info = getDownloader().getPlaylistInfo(request).data()
            for (video in info.videos()) episodes.add(
                create(
                    video.title(),
                    "",
                    "https://www.youtube.com/watch?v=" + video.videoId()
                )
            )
            return episodes
        }

        companion object {
            fun match(url: String?): Boolean {
                return PATTERN_LIST.matcher(url).find()
            }

            fun get(url: String): Parser {
                return Parser(url)
            }
        }
    }

    companion object {
        private const val MPD =
            "<MPD xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns='urn:mpeg:dash:schema:mpd:2011' xsi:schemaLocation='urn:mpeg:dash:schema:mpd:2011 DASH-MPD.xsd' type='static' mediaPresentationDuration='PT%sS' minBufferTime='PT1.500S' profiles='urn:mpeg:dash:profile:isoff-on-demand:2011'>\n" + "<Period duration='PT%sS' start='PT0S'>\n" + "%s\n" + "%s\n" + "</Period>\n" + "</MPD>"
        private const val ADAPT =
            "<AdaptationSet lang='chi'>\n" + "<ContentComponent contentType='%s'/>\n" + "<Representation id='%d' bandwidth='%d' codecs='%s' mimeType='%s' %s>\n" + "<BaseURL>%s</BaseURL>\n" + "<SegmentBase indexRange='%s'>\n" + "<Initialization range='%s'/>\n" + "</SegmentBase>\n" + "</Representation>\n" + "</AdaptationSet>"
        private val PATTERN_VID: Pattern =
            Pattern.compile("(?<=watch\\?v=|youtu.be/|/shorts/|/live/)([\\w-]{11})")
        private val PATTERN_LIST: Pattern = Pattern.compile("(youtube\\.com|youtu\\.be).*list=")

        private var downloader: YoutubeDownloader? = null

        private fun getDownloader(): YoutubeDownloader {
            return (if (downloader == null) YoutubeDownloader(HostOkHttp.client()) else downloader).also {
                downloader = it
            }!!
        }
    }
}
