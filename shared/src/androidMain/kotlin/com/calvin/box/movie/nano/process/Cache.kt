package com.calvin.box.movie.nano.process

import android.text.TextUtils
import com.calvin.box.movie.nano.Nano
import com.github.catvod.utils.Prefers
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.IHTTPSession
import java.util.Objects

class Cache : Process {
    override fun isRequest(session: IHTTPSession, path: String): Boolean {
        return "/cache" == path
    }

    private fun getKey(rule: String?, key: String?): String {
        return "cache_" + (if (TextUtils.isEmpty(rule)) "" else rule + "_") + key
    }

    override fun doResponse(
        session: IHTTPSession,
        path: String,
        files: Map<String, String>
    ): NanoHTTPD.Response {
        val params = session.parms
        val rule = params["rule"]
        val key = params["key"]
        when (Objects.requireNonNullElse(params["do"], "")) {
            "get" -> return Nano.success(Prefers.getString(getKey(rule, key)))
            "set" -> {
                Prefers.put(getKey(rule, key), params["value"])
                return Nano.success()
            }

            "del" -> {
                Prefers.remove(getKey(rule, key))
                return Nano.success()
            }

            else -> return Nano.error(null)
        }
    }
}
