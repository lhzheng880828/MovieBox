package com.calvin.box.movie.api.config

import com.calvin.box.movie.App
import com.calvin.box.movie.SpiderLoader
import com.calvin.box.movie.bean.*
import com.calvin.box.movie.impl.Callback
import com.calvin.box.movie.utils.UrlUtil
//import com.fongmi.android.tv.utils.Notify
import com.calvin.box.movie.bean.Doh
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.getPlatform
//import com.github.catvod.net.OkHttp
import com.calvin.box.movie.utils.Json
import com.calvin.box.movie.getSpiderLoader
import io.github.aakira.napier.Napier
import kotlinx.coroutines.runBlocking
//import com.github.catvod.utils.Util
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.concurrent.Volatile

class VodConfig {
    private var doh: MutableList<Doh> = mutableListOf()
    private var rules: MutableList<Rule> = mutableListOf()
    private var sites: MutableList<Site>  = mutableListOf()
    private var parses: MutableList<Parse> = mutableListOf()
    private var flags: MutableList<String> = mutableListOf()
    private var ads: MutableList<String>  = mutableListOf()

    private var loadLive = false
    private lateinit var config: Config
    private var parse: Parse? = null
    private var wall: String  =  ""
    private var home: Site? = null

    private val spiderLoader:SpiderLoader by lazy {
        getSpiderLoader()
    }

    private object Loader {
        @Volatile
        var INSTANCE: VodConfig = VodConfig()
    }

    private lateinit var wallConfig: WallConfig
    private lateinit var liveConfig: LiveConfig

    fun init(appDataContainer: AppDataContainer): VodConfig {
        this.home = null
        this.parse = null
        Config.setDatabase(appDataContainer)
        this.config = Config.vod()
        wallConfig = appDataContainer.wallRepository
        liveConfig = appDataContainer.liveRepository
        this.ads = mutableListOf()
        this.doh =  mutableListOf()
        this.rules =  mutableListOf()
        this.sites =  mutableListOf()
        this.flags =  mutableListOf()
        this.parses =  mutableListOf()
        this.loadLive = false
        return this
    }



    /*fun config(config: Config): VodConfig {
        this.config = config
        return this
    }*/

   suspend fun clear(): VodConfig {
        this.wall = ""
        this.home = null
        this.parse = null
        ads.clear()
        doh.clear()
        rules.clear()
        sites.clear()
        flags.clear()
        parses.clear()
       spiderLoader.clear()
        this.loadLive = true
        return this
    }

     fun load(callback: Callback?) {
        load(callback, false)
    }

    private fun load(callback: Callback?, cache: Boolean) {
        if (cache) loadConfigCache(callback)//App.execute {  }
        else loadConfig(callback) //App.execute { }
    }

    private fun loadConfig(callback: Callback?) {
        Napier.d { "#loadConfig invoke" }
        try {
            val orgJson = Decoder.getJson(config.url)
           // Napier.d { "#loadConfig orgJson: $orgJson" }
            getPlatform().writeStringToFile("config.json", orgJson)
            checkJson(Json.parse(orgJson).jsonObject, callback)
        } catch (e: Throwable) {
            e.printStackTrace()
            if (config.url.isEmpty()) callback?.error("url is empty")
            else loadCache(callback, e)
        }
    }



    private fun loadCache(callback: Callback?, e: Throwable) {
        Napier.d { "#loadCache invoke" }
        if (config.json.isNotEmpty()) checkJson(Json.parse(config.json).jsonObject, callback)
        else App.post { callback?.error(/*Notify.getError(R.string.error_config_get, e)*/"config get error") }
    }

    private fun loadConfigCache(callback: Callback?) {
        Napier.d { "#loadConfigCache invoke" }
        if (config.json.isNotEmpty() && config.isCache) checkJson(
            Json.parse(config.json).jsonObject, callback
        )
        else loadConfig(callback)
    }

    private fun checkJson(jsonObject: JsonObject, callback: Callback?) {
        Napier.d { "#checkJson invoke" }

        if (jsonObject.containsKey("msg") && callback != null) {
            callback.error(jsonObject["msg"].toString())
        } else if (jsonObject.containsKey("urls")) {
            parseDepot(jsonObject, callback)
        } else {
            parseConfig(jsonObject, callback)
        }
    }

    private fun parseDepot(jsonObject: JsonObject, callback: Callback?) {
        Napier.d { "#parseDepot invoke" }
        val items: List<Depot> = Depot.arrayFrom(jsonObject["urls"].toString())
        val configs: MutableList<Config> =  mutableListOf()
        for (item in items) configs.add(Config.find(item, 0))
        config.url.let { Config.delete(it) }
        config = configs[0]
        loadConfig(callback)
    }

    private fun parseConfig(jsonObject: JsonObject, callback: Callback?) {
        Napier.d { "#parseConfig invoke" }

        try {
            initSite(jsonObject)
            initParse(jsonObject)
            initOther(jsonObject)
            if (loadLive && jsonObject.containsKey("lives")) initLive(jsonObject)
            val spiderStr = Json.safeString(jsonObject, "spider")
            home?.jar = spiderStr
            spiderLoader.parseJar("", spiderStr)
            config.logo(Json.safeString(jsonObject, "logo"))
            config.json(jsonObject.toString()).update()
            callback?.success()
        } catch (e: Throwable) {
            e.printStackTrace()
            callback?.error(/*Notify.getError(R.string.error_config_parse, e)*/"config parse error")
        }
    }

    private fun initSite(jsonObject: JsonObject) {
        if (jsonObject.containsKey("video")) {
            val jsonElement = jsonObject["video"]
            if(jsonElement!=null){
                initSite(jsonElement.jsonObject)
            }
            return
        }
        for (element in Json.safeListElement(jsonObject, "sites")) {
            val site: Site = Site.objectFrom(element)
            //Napier.d { "initSite: loop site: $site" }
            if (sites.contains(site)) continue
            site.api = (parseApi(site.api))
            site.ext = (parseExt(site.ext))
            sites.add(site.trans().sync())
        }
        for (site in sites) {
            //if (site.key == config.home) {
            if (site.key == "索尼") {
                setHome(site)
            }
        }
    }

    private fun initLive(jsonObject: JsonObject) {
        Napier.d { "#initLive invoke" }
        val temp: Config = runBlocking { Config.find(config, 1).save()}
        val sync: Boolean = liveConfig.needSync(config.url)
        if (sync) liveConfig.clear().config(temp).parse(jsonObject)
    }

    private fun initParse(jsonObject: JsonObject) {
        for (element in Json.safeListElement(jsonObject, "parses")) {
            val parse: Parse = Parse.objectFrom(element)
            if (parse.name == config.parse && parse.type > 1) setParse(parse)
            if (!parses.contains(parse)) parses.add(parse)
        }
    }

    private fun initOther(`object`: JsonObject) {
        if (parses.size > 0) parses.add(0, Parse.god())
        if (home == null) setHome(if (sites.isEmpty()) Site() else sites.find { it.key == "索尼"/*"csp_Wogg"*/ }!! )
        if (parse == null) setParse(if (parses.isEmpty()) Parse() else parses[0])
        setRules(Rule.arrayFrom(`object`["rules"]).toMutableList())
        setDoh(Doh.arrayFrom(`object`["doh"]).toMutableList())
        setFlags(Json.safeListString(`object`, "flags"))
        setWall(Json.safeString(`object`, "wallpaper"))
        setAds(Json.safeListString(`object`, "ads").toMutableList())
    }

    private fun parseApi(api: String): String {
        if (api.startsWith("file") || api.startsWith("clan") || api.startsWith("assets")) return UrlUtil.convert(api)
        return api
    }

    private fun parseExt(ext: String): String {
        if (ext.startsWith("file") || ext.startsWith("clan") || ext.startsWith("assets")) return UrlUtil.convert(ext)
        if (ext.startsWith("img+")) return Decoder.getExt(ext)
        return ext
    }



    /*@Throws(Throwable::class)
    fun jsonExt(key: String?, jxs: java.util.LinkedHashMap<String?, String?>?, url: String?): JsonObject {
        return jarLoader.jsonExt(key, jxs, url)
    }

    @Throws(Throwable::class)
    fun jsonExtMix(
        flag: String?,
        key: String?,
        name: String?,
        jxs: java.util.LinkedHashMap<String?, java.util.HashMap<String?, String?>?>?,
        url: String?
    ): JsonObject {
        return jarLoader.jsonExtMix(flag, key, name, jxs, url)
    }*/

    fun getDoh(): List<Doh> {
        val items: MutableList<Doh> = Doh.get()
        items.removeAll(doh)
        items.addAll(doh)
        return items
    }

    fun setDoh(doh: MutableList<Doh>) {
        this.doh = doh
    }

    fun getRules(): List<Rule> {
        return rules
    }

    fun setRules(rules: MutableList<Rule>) {
       // for (rule in rules) if ("proxy" == rule.name) OkHttp.selector().setHosts(rule.hosts)
        rules.remove(Rule.create("proxy"))
        this.rules = rules
    }

    fun getSites(): List<Site> {
        return sites
    }

    fun getParses(): List<Parse> {
        return parses
    }

    fun getParses(type: Int): List<Parse> {
        val items: MutableList<Parse> =  mutableListOf()
        for (item in getParses()) if (item.type == type) items.add(item)
        return items
    }

    fun getParses(type: Int, flag: String?): List<Parse> {
        val items: MutableList<Parse> = mutableListOf()
        for (item in getParses(type)) if (item.ext.flag.isEmpty() || item.ext.flag
                .contains(flag)
        ) items.add(item)
        if (items.isEmpty()) items.addAll(getParses(type))
        return items
    }

    fun getFlags(): List<String> {
        return flags
    }

    private fun setFlags(flags: List<String>) {
        this.flags.addAll(flags)
    }

    fun getAds(): List<String> {
        return ads
    }

    private fun setAds(ads: MutableList<String>) {
        this.ads = ads
    }

    fun getConfig(): Config {
        return config
    }

    fun getParse(): Parse {
        return parse ?: Parse()
    }

    fun getHome(): Site? {
        return home
    }

    fun getWall(): String {
        return wall
    }

    fun getParse(name: String): Parse? {
        val index = getParses().indexOf(Parse.get(name))
        return if (index == -1) null else getParses()[index]
    }

    fun getSite(key: String): Site {
        val index = getSites().indexOf(Site.get(key))
        return if (index == -1) Site() else getSites()[index]
    }

    private fun setParse(parse: Parse) {
        this.parse = parse
        this.parse?.isActivated = true
        runBlocking { config.parse(parse.name).save() }
        for (item in getParses()) item.setActivated(parse)
    }

    private fun setHome(home: Site) {
        this.home = home
        this.home!!.activated =true
        runBlocking { config.home(home.key).save() }
        for (item in getSites()) item.setActivated(home)
    }

    private fun setWall(wall: String) {
        this.wall = wall
        val load = wall.isNotEmpty() && wallConfig.needSync(wall)
        if (load) wallConfig.config(Config.find(wall, config.name, 2).update())
    }


    companion object {
        fun get(): VodConfig {
            return Loader.INSTANCE
        }

        val cid: Int
            get() = get().getConfig().id

        val url: String
            get() = get().getConfig().url

        val desc: String
            get() = get().getConfig().desc

        val homeIndex: Int
            get() = get().getSites().indexOf(get().getHome())

        fun hasParse(): Boolean {
            return get().getParses().isNotEmpty()
        }

        fun load(config: Config, callback: Callback?) {
           // get().clear().config(config).load(callback)
        }
    }
}
