package com.calvin.box.movie.nano.process

import android.os.Environment
import android.text.TextUtils
import com.calvin.box.movie.App
import com.calvin.box.movie.Constant
import com.calvin.box.movie.api.config.LiveConfig
import com.calvin.box.movie.api.config.VodConfig
import com.calvin.box.movie.api.config.WallConfig
import com.calvin.box.movie.bean.*
import com.calvin.box.movie.event.*
import com.calvin.box.movie.impl.Callback
import com.calvin.box.movie.nano.Nano
import com.calvin.box.movie.utils.FileUtil
import com.calvin.box.movie.utils.Notify
import com.github.catvod.net.OkHttp
import com.github.catvod.utils.Path
import fi.iki.elonen.NanoHTTPD
import io.github.aakira.napier.Napier
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import okhttp3.MediaType
import java.io.File
import java.util.Locale
import java.util.Objects

class Action : Process {
    override fun isRequest(session: NanoHTTPD.IHTTPSession, path: String): Boolean {
        return "/action" == path
    }

    override fun doResponse(
        session: NanoHTTPD.IHTTPSession,
        path: String,
        files: Map<String, String>
    ): NanoHTTPD.Response {
        val params: Map<String, String?> = session.getParms()
        when (Objects.requireNonNullElse(params["do"], "")) {
            "search" -> {
                onSearch(params)
                return Nano.success()
            }

            "push" -> {
                onPush(params)
                return Nano.success()
            }

            "setting" -> {
                onSetting(params)
                return Nano.success()
            }

            "file" -> {
                onFile(params)
                return Nano.success()
            }

            "refresh" -> {
                onRefresh(params)
                return Nano.success()
            }

            "cast" -> {
                onCast(params)
                return Nano.success()
            }

            "sync" -> {
                onSync(params)
                return Nano.success()
            }

            "transmit" -> {
                onTransmit(params, files)
                return Nano.success()
            }

            else -> return Nano.error(null)
        }
    }

    private fun onSearch(params: Map<String, String?>) {
        val word = params["word"]
        if (TextUtils.isEmpty(word)) return
       // ServerEvent.search(word)
    }

    private fun onPush(params: Map<String, String?>) {
        val url = params["url"]
        if (TextUtils.isEmpty(url)) return
        //ServerEvent.push(url)
    }

    private fun onSetting(params: Map<String, String?>) {
        val text = params["text"]
        val name = params["name"]
        if (TextUtils.isEmpty(text)) return
       // ServerEvent.setting(text, name)
    }

    private fun onFile(params: Map<String, String?>) {
        val path = params["path"]
        if (TextUtils.isEmpty(path)) return
        if (path!!.endsWith(".xml")) RefreshEvent.danmaku(path)
        else if (path.endsWith(".apk")) FileUtil.openFile(Path.local(path))
        else if (path.endsWith(".srt") || path.endsWith(".ssa") || path.endsWith(".ass")) RefreshEvent.subtitle(
            path
        )
        else ServerEvent.setting(path)
    }

    private fun onRefresh(params: Map<String, String?>) {
        val type = params["type"]
        val path = params["path"]
        if (TextUtils.isEmpty(type)) return
        when (type) {
            "detail" -> RefreshEvent.detail()
            "player" -> RefreshEvent.player()
            "danmaku" -> RefreshEvent.danmaku(path)
            "subtitle" -> RefreshEvent.subtitle(path)
        }
    }

    private fun onCast(params: Map<String, String?>) {
        val configJson = params["config"]
        val deviceJson = params["device"]
        val historyJson = params["history"]
        val config = configJson?.let {
            Config.objectFrom(
                it
            )
        }
        val device: Device? = deviceJson?.let { Device.objectFrom(it) }
        val history: History? = historyJson?.let { History.objectFrom(it) }
        val posConfig = config?:Config(type = Config.TYPE.VOD.ordinal)
        val posDevice = device ?: Device()
        val posHistory = history?: History()
        CastEvent.post(Config.find(config!!), posDevice, posHistory)
    }

    private fun onSync(params: Map<String, String?>) {
        val keep = params["type"] == "keep"
        val force = params["force"] == "true"
        val history = params["type"] == "history"
        val device = params["device"]
        val mode = Objects.requireNonNullElse(params["mode"], "0")
        if (device != null && (mode == "0" || mode == "2")) {
            val dev: Device = Device.objectFrom(device)
            if (history) sendHistory(dev, params)
            else if (keep) sendKeep(dev)
        }
        if (mode == "0" || mode == "1") {
            if (history) syncHistory(params, force)
            else if (keep) syncKeep(params, force)
        }
    }

    private fun onTransmit(params: Map<String, String?>, files: Map<String, String>) {
        val type = params["type"]
        when (type) {
            "apk" -> apk(params, files)
            "vod_config" -> vodConfig(params)
            "wall_config" -> wallConfig(params, files)
            "push_restore" -> pushRestore(params, files)
            "pull_restore" -> pullRestore(params, files)
            else -> {}
        }
    }

    private fun sendHistory(device: Device, params: Map<String, String?>) {
        try {
            val configJson = params["config"]
            val targetsJson = params["targets"]
            if(configJson.isNullOrEmpty() || targetsJson.isNullOrEmpty()){
                return
            }
            val parsedConfig = Config.objectFrom(configJson) ?: return
            val config = Config.find(parsedConfig)
            val body: FormBody.Builder = FormBody.Builder()
            body.add("config", config.toString())
            val json = Json { ignoreUnknownKeys=true }
            body.add("targets", json.encodeToString(History.get(config.id)))
            OkHttp.newCall(
                OkHttp.client(Constant.TIMEOUT_SYNC),
                device.ip+("/action?do=sync&mode=0&type=history"),
                body.build()
            ).execute()
        } catch (e: Exception) {
            App.post { Notify.show(e.message) }
        }
    }

    private fun sendKeep(device: Device) {
        try {
            val body: FormBody.Builder = FormBody.Builder()
            val json = Json { ignoreUnknownKeys=true }
            body.add("targets", json.encodeToString(Keep.getVod()))
            body.add("configs", json.encodeToString(Config.findUrls()))
            OkHttp.newCall(
                OkHttp.client(Constant.TIMEOUT_SYNC),
                device.ip.concat("/action?do=sync&mode=0&type=keep"),
                body.build()
            ).execute()
        } catch (e: Exception) {
            App.post { Notify.show(e.message) }
        }
    }

    fun syncHistory(params: Map<String, String?>, force: Boolean) {

        val configJson = params["config"]
        val targetsJson = params["targets"]
        if(configJson.isNullOrEmpty() || targetsJson.isNullOrEmpty()){
            return
        }
        val parsedConfig = Config.objectFrom(configJson) ?: return

        val config = Config.find(parsedConfig)
        val targets: List<History> = History.arrayFrom(targetsJson)
        if (VodConfig.get().getConfig().equals(config)) {
            if (force) History.delete(config.id)
            History.sync(targets)
        } else {
            VodConfig.load(config, getCallback(targets))
        }
    }

    private fun getCallback(targets: List<History>): Callback {
        return object : Callback {
            override fun success() {
                RefreshEvent.config()
                RefreshEvent.video()
                History.sync(targets)
            }

            override fun error(msg: String) {
                Notify.show(msg)
            }
        }
    }

    private fun syncKeep(params: Map<String, String?>, force: Boolean) {
        if(params["targets"] == null){
            return
        }
        val targets: List<Keep> = Keep.arrayFrom(params["targets"]!!)
        val configs = Config.arrayFrom(
            params["configs"]!!
        )
        if (TextUtils.isEmpty(VodConfig.url) && configs.isNotEmpty()) {
            VodConfig.load(Config.find(configs[0]), getCallback(configs, targets))
        } else {
            if (force) Keep.deleteAll()
            Keep.sync(configs, targets)
        }
    }

    private fun getCallback(configs: List<Config>, targets: List<Keep>): Callback {
        return object : Callback {
           override fun success() {
                RefreshEvent.history()
                RefreshEvent.config()
                RefreshEvent.video()
                Keep.sync(configs, targets)
            }

            override fun error(msg: String) {
                Notify.show(msg)
            }
        }
    }

    private fun apk(params: Map<String, String?>, files: Map<String, String>) {
        for (k in files.keys) {
            val fn = params[k]
            val temp = File(files[k])
            if (!temp.exists()) continue
            if (fn!!.lowercase(Locale.getDefault()).endsWith(".apk")) {
                val apk = Path.cache(System.currentTimeMillis().toString() + "-" + fn)
                Path.copy(temp, apk)
                FileUtil.openFile(apk)
            }
            temp.delete()
            break
        }
    }

    private fun vodConfig(params: Map<String, String?>) {
        val url = params["url"]
        if (TextUtils.isEmpty(url)) return
       /* App.post { Notify.progress(App.activity()) }
        VodConfig.get().load(Config.find(url!!, 0), callback)*/
    }

    private fun wallConfig(params: Map<String, String?>, files: Map<String, String>) {
        for (k in files.keys) {
            val fn = params[k]
            val temp = File(files[k])
            if (!temp.exists()) continue
            val wall = File(Path.download(), fn)
            Path.copy(temp, wall)
            //App.post { Notify.progress(App.activity()) }
            WallConfig.load(
                Config.find("file://" + Environment.DIRECTORY_DOWNLOADS + "/" + fn, 2),
                object : Callback{
                    override fun success() {
                        Notify.dismiss()
                    }

                    override fun error(msg: String) {
                        Notify.dismiss()
                        Notify.show(msg)
                    }

                })
            temp.delete()
            break
        }
    }

    private fun pushRestore(params: Map<String, String?>, files: Map<String, String>) {
        for (k in files.keys) {
            val fn = params[k]
            val temp = File(files[k])
            if (!temp.exists()) continue
            val restore = Path.cache(System.currentTimeMillis().toString() + "-" + fn)
            Path.copy(temp, restore)
            /*AppDatabase.restore(restore, object : Callback {
               override fun success() {
                    App.post { Notify.progress(App.activity()) }
                    App.post({
                        AppDatabase.reset()
                        initConfig()
                    }, 3000)
                }

                override fun error(msg: String) {
                   Napier.d { "pushRestore, error msg: $msg" }
                }

            })*/
            temp.delete()
            break
        }
    }

    private fun pullRestore(params: Map<String, String?>, files: Map<String, String>) {
        val ip = params["ip"]
        if (TextUtils.isEmpty(ip)) return
        /*AppDatabase.backup(object : Callback {
            override fun success(path: String?) {
                val type = "push_restore"
                val file = File(path)
                val mediaType: MediaType = parse.parse("multipart/form-data")
                val body: Builder = Builder()
                body.setType(MultipartBody.FORM)
                body.addFormDataPart("name", file.name)
                body.addFormDataPart("files-0", file.name, RequestBody.create(mediaType, file))
                OkHttp.newCall(
                    OkHttp.client(Constant.TIMEOUT_TRANSMIT),
                    "$ip/action?do=transmit&type=$type",
                    body.build()
                ).enqueue(
                    this.callback
                )
            }
        })*/
    }

    private val callback: Callback
        get() = object : Callback {
            override fun success() {
                Notify.dismiss()
                RefreshEvent.history()
                RefreshEvent.config()
                RefreshEvent.video()
            }

            override fun error(msg: String) {
                Notify.dismiss()
                Notify.show(msg)
            }
        }

    private fun initConfig() {
        WallConfig.get()
        LiveConfig.get().load()
        VodConfig.get().load(callback)
    }
}
