package com.calvin.box.movie.player.extractor

import android.net.Uri
import android.os.SystemClock
import com.calvin.box.movie.bean.Episode
import com.calvin.box.movie.player.Extractor
import com.calvin.box.movie.utils.AndroidDownload
import com.calvin.box.movie.utils.UrlUtil
import com.github.catvod.utils.HostUtil
import com.github.catvod.utils.HostUtil.md5
import com.github.catvod.utils.Path
import com.xunlei.downloadlib.Util
import com.xunlei.downloadlib.XLTaskHelper
import com.xunlei.downloadlib.parameter.GetTaskId
import com.xunlei.downloadlib.parameter.TorrentFileInfo
import com.xunlei.downloadlib.parameter.XLTaskInfo
import java.io.File
import java.util.Arrays
import java.util.Objects
import java.util.concurrent.Callable
import java.util.regex.Pattern

class Thunder : Extractor {
    private var taskId: GetTaskId? = null

    override fun match(scheme: String?, host: String?): Boolean {
        return "magnet" == scheme || "ed2k" == scheme
    }

    @Throws(Exception::class)
    override fun fetch(url: String?): String {
        return if (UrlUtil.scheme(url) == "magnet"
        ) addTorrentTask(Uri.parse(url)) else addThunderTask(url)
    }

    @Throws(Exception::class)
    private fun addTorrentTask(uri: Uri): String {
        val torrent = File(uri.path)
        val name = uri.getQueryParameter("name")
        val index = uri.getQueryParameter("index")!!.toInt()
        taskId = XLTaskHelper.get()
            .addTorrentTask(torrent, Objects.requireNonNull(torrent.parentFile), index)
        while (true) {
            val taskInfo: XLTaskInfo = XLTaskHelper.get().getBtSubTaskInfo(taskId, index).mTaskInfo
            if (taskInfo.mTaskStatus == 3) throw Exception(taskInfo.errorMsg)
            if (taskInfo.mTaskStatus != 0) return XLTaskHelper.get()
                .getLocalUrl(File(torrent.parent, name))
            else SystemClock.sleep(300)
        }
    }

    private fun addThunderTask(url: String?): String {
        val folder = Path.thunder(md5(url))
        taskId = XLTaskHelper.get().addThunderTask(url, folder)
        return XLTaskHelper.get().getLocalUrl(taskId!!.saveFile)
    }

    override fun stop() {
        if (taskId == null) return
        XLTaskHelper.get().deleteTask(taskId)
        taskId = null
    }

    override fun exit() {
        XLTaskHelper.get().release()
    }

    class Parser(private val url: String) : Callable<List<Episode>> {
        private var time = 0

        private fun sleep() {
            SystemClock.sleep(10)
            time += 10
        }

        override fun call(): List<Episode> {
            val torrent = isTorrent(
                url
            )
            val episodes: MutableList<Episode> = ArrayList<Episode>()
            val taskId: GetTaskId = XLTaskHelper.get().parse(
                url, Path.thunder(
                    HostUtil.md5(url)
                )
            )
            if (!torrent && !taskId.getRealUrl()
                    .startsWith("magnet")
            ) return Arrays.asList(Episode.create(taskId.fileName, taskId.realUrl))
            if (torrent) AndroidDownload.create(url, taskId.saveFile).start()
            else while (XLTaskHelper.get().getTaskInfo(taskId)
                    .getTaskStatus() != 2 && time < 5000
            ) sleep()
            val medias: List<TorrentFileInfo> =
                XLTaskHelper.get().getTorrentInfo(taskId.getSaveFile()).getMedias()
            for (media in medias) episodes.add(
                Episode.Companion.create(
                    media.getFileName(),
                    media.getSize(),
                    media.getPlayUrl()
                )
            )
            XLTaskHelper.get().stopTask(taskId)
            return episodes
        }

        companion object {
            private val THUNDER: Pattern = Pattern.compile("(magnet|thunder|ed2k):.*")
            fun match(url: String): Boolean {
                return THUNDER.matcher(url).find() || isTorrent(url)
            }

            fun get(url: String): Parser {
                return Parser(url)
            }

            private fun isTorrent(url: String): Boolean {
                return !url.startsWith("magnet") && url.split(";".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0].endsWith(".torrent")
            }
        }
    }
}
