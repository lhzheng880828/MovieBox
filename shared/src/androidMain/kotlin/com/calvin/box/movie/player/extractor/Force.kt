package com.calvin.box.movie.player.extractor

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import android.os.SystemClock
import com.calvin.box.movie.ContextProvider
import com.calvin.box.movie.player.Extractor
import com.forcetech.Util
import com.github.catvod.net.HostOkHttp
import com.google.common.net.HttpHeaders
import okhttp3.Headers
import okhttp3.Headers.Companion.headersOf

class Force : Extractor {
    private val set = HashSet<String>()

    private val context = ContextProvider.context as Context

    override fun match(scheme: String?, host: String?): Boolean {
        return "push" != scheme && scheme!!.startsWith("p") || "mitv" == scheme
    }

    private fun init(scheme: String) {
        context.bindService(Util.intent(context, scheme), mConn, Context.BIND_AUTO_CREATE)
    }

    @Throws(Exception::class)
    override fun fetch(url: String?): String? {
        if(url==null) return ""
        val scheme = Util.scheme(url)
        if (!set.contains(scheme)) init(scheme)
        while (!set.contains(scheme)) SystemClock.sleep(10)
        val uri = Uri.parse(url)
        val port = Util.port(scheme)
        val id = uri.lastPathSegment
        val cmd =
            "http://127.0.0.1:" + port + "/cmd.xml?cmd=switch_chan&server=" + uri.host + ":" + uri.port + "&id=" + id
        val result = "http://127.0.0.1:$port/$id"
        HostOkHttp.newCall(cmd, headersOf(HttpHeaders.USER_AGENT, "MTV")).execute()
            .body!!.string()
        return result
    }

    override fun stop() {
    }

    override fun exit() {
        try {
            if (set.isNotEmpty()) context.unbindService(mConn)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            set.clear()
        }
    }

    private val mConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            set.add(Util.trans(name))
        }

        override fun onServiceDisconnected(name: ComponentName) {
            set.remove(Util.trans(name))
        }
    }
}
