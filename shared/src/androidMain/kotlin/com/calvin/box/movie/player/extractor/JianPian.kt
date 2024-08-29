package com.calvin.box.movie.player.extractor

import android.net.Uri
import com.calvin.box.movie.player.Extractor
import com.github.catvod.utils.Path
import com.p2p.P2PClass
import java.net.URLDecoder
import java.net.URLEncoder

class JianPian : Extractor {
    private var p2p: P2PClass? = null
    private var path: String? = null
    private var pathPaused: MutableMap<String?, Boolean>? = null

    override fun match(scheme: String?, host: String?): Boolean {
        return "tvbox-xg" == scheme || "jianpian" == scheme || "ftp" == scheme
    }

    private fun init() {
        if (p2p == null) p2p = P2PClass()
        if (pathPaused == null) pathPaused = HashMap()
    }

    @Throws(Exception::class)
    override fun fetch(url: String?): String {
        init()
        stop()
        start(url)
        return "http://127.0.0.1:" + p2p!!.port + "/" + URLEncoder.encode(
            Uri.parse(path).lastPathSegment,
            "GBK"
        )
    }

    private fun start(url: String?) {
        try {
            val lastPath = path
            path = URLDecoder.decode(url).split("\\|".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[0]
            path = path!!.replace("jianpian://pathtype=url&path=", "")
            path = path!!.replace("tvbox-xg://", "").replace("tvbox-xg:", "")
            path = path!!.replace("xg://", "ftp://").replace("xgplay://", "ftp://")
            val isDiff = lastPath != null && lastPath != path
            if (isDiff) p2p!!.P2Pdoxdel(lastPath!!.toByteArray(charset("GBK")))
            p2p!!.P2Pdoxstart(path!!.toByteArray(charset("GBK")))
            if (lastPath == null || isDiff) p2p!!.P2Pdoxadd(path!!.toByteArray(charset("GBK")))
            if (isDiff && pathPaused!!.containsKey(lastPath)) pathPaused!!.remove(lastPath)
            pathPaused!![path] = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun stop() {
        try {
            if (p2p == null || path == null) return
            if (pathPaused!!.containsKey(path) && java.lang.Boolean.TRUE == pathPaused!![path]) return
            p2p!!.P2Pdoxpause(path!!.toByteArray(charset("GBK")))
            pathPaused!![path] = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun exit() {
        Path.clear(Path.jpa())
    }
}
