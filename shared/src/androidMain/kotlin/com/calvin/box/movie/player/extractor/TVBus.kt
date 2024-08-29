package com.calvin.box.movie.player.extractor

import android.content.Context
import com.calvin.box.movie.ContextProvider
import com.calvin.box.movie.api.config.LiveConfig.Companion.get
import com.calvin.box.movie.bean.Core
import com.calvin.box.movie.player.Extractor
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tvbus.engine.Listener
import com.tvbus.engine.TVCore

class TVBus : Extractor, Listener {
    private var tvcore: TVCore? = null
    private var hls: String? = null
    private var core: Core? = null

    private val context = ContextProvider.context as Context

    override fun match(scheme: String?, host: String?): Boolean {
        return "tvbus" == scheme
    }

    private fun init(core: Core) {
        //App.get().setHook(core.hook());
        tvcore = TVCore(core.so)
        tvcore!!.auth(core.auth).broker(core.broker)
        tvcore!!.name(core.name).pass(core.pass)
        tvcore!!.serv(0).play(8902).mode(1).listener(this)
        //App.get().setHook(false);
        tvcore!!.init()
    }

    override fun fetch(url: String?): String? {
        try {
            if (core != null && core != get().getHome().core) change()
            if (tvcore == null) init(get().getHome().core.also { core = it })
            tvcore!!.start(url)
            onWait()
            onCheck()
            return hls
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Throws(Exception::class)
    private fun onCheck() {
        if (hls!!.startsWith("-")) throw Exception("Error Code : $hls")
    }

    @Throws(InterruptedException::class)
    private fun onWait() {
        synchronized(this) {
            (this as Object).wait()
        }
    }

    private fun onNotify() {
        synchronized(this) {
            (this as Object).notify()
        }
    }

    private fun change() {
        // Setting.putBootLive(true);
        // App.post(() -> System.exit(0), 250);
    }

    override fun stop() {
        if (tvcore != null) tvcore!!.stop()
        if (hls != null) hls = null
    }

    override fun exit() {
        if (tvcore != null) tvcore!!.quit()
        tvcore = null
    }

    override fun onPrepared(result: String) {
        val json = Gson().fromJson(result, JsonObject::class.java)
        if (json["hls"] == null) return
        hls = json["hls"].asString
        onNotify()
    }

    override fun onStop(result: String) {
        val json = Gson().fromJson(result, JsonObject::class.java)
        hls = json["errno"].asString
        if (hls!!.startsWith("-")) onNotify()
    }

    override fun onInited(result: String) {
    }

    override fun onStart(result: String) {
    }

    override fun onInfo(result: String) {
    }

    override fun onQuit(result: String) {
    }
}
