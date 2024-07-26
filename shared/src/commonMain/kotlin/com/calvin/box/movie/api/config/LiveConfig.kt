package com.calvin.box.movie.api.config

import com.calvin.box.movie.App
import com.calvin.box.movie.Setting
import com.calvin.box.movie.api.LiveParser
import com.calvin.box.movie.bean.*
import com.calvin.box.movie.db.MoiveDatabase
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.impl.Callback
//import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.jvm.JvmStatic

/*import com.fongmi.android.tv.R
import com.fongmi.android.tv.Setting
import com.fongmi.android.tv.ui.activity.LiveActivity
import com.fongmi.android.tv.utils.Notify*/
import com.calvin.box.movie.utils.Json

import kotlinx.serialization.json.jsonObject

class LiveConfig private constructor() {

    private var lives: MutableList<Live> = mutableListOf()
    private lateinit var config: Config
    private var sync: Boolean = false
    private var home: Live? = null

    companion object {
        @JvmStatic
        fun get(): LiveConfig = Loader.INSTANCE

        @JvmStatic
        fun getUrl(): String = get().getConfig().url ?: ""

        @JvmStatic
        fun getDesc(): String = get().getConfig().desc ?: ""

        @JvmStatic
        fun getResp(): String = get().getHome().core.resp ?: ""

        @JvmStatic
        fun getHomeIndex(): Int = get().getLives().indexOf(get().getHome())

        @JvmStatic
        fun isOnly(): Boolean = get().getLives().size == 1

        @JvmStatic
        fun isEmpty(): Boolean = false

        @JvmStatic
        fun hasUrl(): Boolean = getUrl().isNotEmpty()

        @JvmStatic
        fun load(config: Config, callback: Callback) {
            get().clear().config(config).load(callback)
        }
    }

    private object Loader {
        val INSTANCE = LiveConfig()
    }

    fun init(appDataContainer: AppDataContainer): LiveConfig {
        this.home = null
        Config.setDatabase(appDataContainer)
        return config(Config.live())
    }

    fun config(config: Config): LiveConfig {
        this.config = config
        this.sync = config.url == VodConfig.url
        return this
    }

    fun clear(): LiveConfig {
        getLives().clear()
        this.home = null
        return this
    }

    fun load() {
        if (isEmpty()) load(object :Callback{
            override fun success() {
            }

            override fun error(msg: String) {
            }

        })
    }

    fun load(callback: Callback) {
        App.execute {
            loadConfig(callback)
        }
    }

    private fun loadConfig(callback: Callback) {
        try {
            val text = Decoder.getJson(config.url)
            parseConfig(text, callback)
        } catch (e: Throwable) {
            if (config.url.isEmpty()) {
                App.post { callback.error("") }
            } else {
                App.post { callback.error(/*Notify.getError(R.string.error_config_get, e)*/"get config error") }
            }
            e.printStackTrace()
        }
    }

    private fun parseConfig(text: String, callback: Callback) {
        if (Json.invalid(text)) {
            parseText(text, callback)
        } else {
            checkJson(Json.parse(text).jsonObject, callback)
        }
    }

    private fun parseText(text: String, callback: Callback) {
        val live = Live(config.url).sync()
        LiveParser.text(live, text)
        getLives().remove(live)
        getLives().add(live)
        setHome(live, true)
        callback.success()
    }

    private fun checkJson(json: JsonObject, callback: Callback) {
        when {
            json.containsKey("msg") -> {
                App.post { callback.error(json["msg"].toString()) }
            }

            json.containsKey("urls") -> {
                parseDepot(json, callback)
            }

            else -> {
                parseConfig(json, callback)
            }
        }
    }

    private fun parseDepot(json: JsonObject, callback: Callback) {
        val items = Depot.arrayFrom(json["urls"].toString())
        val configs = items.map { Config.find(it, 1) }
        Config.delete(config.url)
        config = configs.first()
        loadConfig(callback)
    }

    private fun parseConfig(json: JsonObject, callback: Callback?) {
        val lives = Json.safeListElement(json, "lives")
        if (lives.isNotEmpty()) {
            lives.forEach { add(Live.objectFrom(it).check()) }
        }
        getLives().firstOrNull { it.name == config.home }?.let { setHome(it, true) }
        if (home == null) {
            setHome(if (getLives().isEmpty()) Live() else getLives().first(), true)
        }
        callback?.success()
    }

    private fun add(live: Live) {
        if (!getLives().contains(live)) {
            getLives().add(live.sync())
        }
    }

    private fun bootLive() {
       /* Setting.putBootLive(false)
        LiveActivity.start(App.get())*/
    }

    fun parse(json: JsonObject) {
        parseConfig(json, null)
    }

    fun setKeep(channel: Channel) {
       /* if (home == null || channel.group.isHidden || channel.urls.isEmpty()) return
        Setting.putKeep("${home?.name}${AppDatabase.SYMBOL}${channel.group.name}${AppDatabase.SYMBOL}${channel.name}${AppDatabase.SYMBOL}${channel.current}")*/
    }

    fun setKeep(items: List<Group>) {
        val keys = Keep.getLive().map { it.key }
        items.forEach { group ->
            if (!group.isKeep()) {
                group.channels.filter { keys.contains(it.name) }
                    .forEach { items.first().add(it) }
            }
        }
    }

    fun find(items: List<Group>): IntArray {
        val splits = Setting.getKeep().split(MoiveDatabase.SYMBOL)
        if (splits.size < 4 || getHome().name != splits[0]) return intArrayOf(1, 0)
        items.forEachIndexed { i, group ->
            if (group.name == splits[1]) {
                val j = group.find(splits[2])
                if (j != -1) {
                    if (splits.size == 4) group.channels[j].setLine(splits[3])
                    return intArrayOf(i, j)
                }
            }
        }
        return intArrayOf(1, 0)
    }

    fun find(number: String, items: List<Group>): IntArray {
        items.forEachIndexed { i, group ->
            val j = group.find(number.toInt())
            if (j != -1) return intArrayOf(i, j)
        }
        return intArrayOf(-1, -1)
    }

    fun needSync(url: String): Boolean {
        return sync || config.url.isEmpty() || url == config.url
    }

    fun getLives(): MutableList<Live> {
        return lives
    }

    fun getConfig(): Config {
        return config ?: Config.live()
    }

    fun getHome(): Live {
        return home ?: Live()
    }

    fun setHome(home: Live) {
        setHome(home, false)
    }

    private fun setHome(home: Live, check: Boolean) {
        this.home = home
        this.home?.activated = true
        config.home(home.name).update()
        getLives().forEach { it.setActived(home)  }
       /* if (App.activity() != null && App.activity() is LiveActivity) return
        if (check && (home.isBoot || Setting.isBootLive())) {
            App.post { bootLive() }
        }*/
    }
}

