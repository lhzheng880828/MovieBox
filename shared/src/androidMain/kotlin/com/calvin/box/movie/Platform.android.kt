package com.calvin.box.movie

import android.app.Application
import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.collection.ArrayMap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.calvin.box.movie.api.config.VodConfig
import com.calvin.box.movie.api.loader.JsLoader
import com.calvin.box.movie.api.loader.PyLoader
import com.calvin.box.movie.bean.Channel
import com.calvin.box.movie.bean.Config
import com.calvin.box.movie.bean.Doh
import com.calvin.box.movie.bean.Flag
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.bean.Url
import com.calvin.box.movie.bean.Vod
import com.calvin.box.movie.db.MoiveDatabase
import com.calvin.box.movie.db.addMoiveMigrations
import com.calvin.box.movie.nano.Server
import com.calvin.box.movie.player.AndroidSource
import com.calvin.box.movie.player.Players
import com.calvin.box.movie.player.Source
import com.calvin.box.movie.pref.AndroidPref
import com.calvin.box.movie.pref.BasePreference
import com.calvin.box.movie.utils.FileDownloader
import com.calvin.box.movie.utils.LanguageUtil
import com.calvin.box.movie.utils.Sniffer
import com.calvin.box.movie.utils.restartApp
import com.github.catvod.Init
import com.github.catvod.crawler.Spider
import com.github.catvod.crawler.SpiderDebug
import com.github.catvod.crawler.SpiderNull
import com.github.catvod.net.HostOkHttp
import com.github.catvod.utils.Asset
import com.github.catvod.utils.HostUtil
import com.github.catvod.utils.Path
import com.github.catvod.utils.Shell
import com.github.catvod.utils.Trans
import dalvik.system.DexClassLoader
import io.github.aakira.napier.Napier
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Headers
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    override fun getDefaultCountry(): String {
        return java.util.Locale.getDefault().country
    }

    override fun getPlatformRegex(pattern: String): Regex {
      return  Regex(pattern)
    }


    override fun getUriQuery(url: String): String? {
         return Uri.parse(url).query
    }

    override fun url2FileName(url: String): String {
         return if (url.startsWith("file")) {
            File(url).name
        } else {
            Uri.parse(url).lastPathSegment ?: ""
        }
    }

    override fun writeStringToFile(fileName: String, content: String) {
            try {
                // 获取文件目录
                val fileDir = appContext.filesDir
                // 创建文件对象
                val file = File(fileDir, fileName)

                // 使用 FileOutputStream 写入数据
                FileOutputStream(file).use { fos ->
                    OutputStreamWriter(fos).use { writer ->
                        writer.write(content)
                        writer.flush()  // 确保所有数据都被写入文件
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    override fun setDoh(doh: Doh) {
        Source.stop();
        HostOkHttp.get().setDoh(com.github.catvod.bean.Doh().name(doh.name).url(doh.url).ips(doh.ips));
        //Notify.progress(getActivity());
        VodConfig.load(Config.vod(), null);
    }

    override fun setProxy(proxy: String) {
        Source.stop();
        HostOkHttp.selector().clear();
        HostOkHttp.get().setProxy(proxy);
        //Notify.progress(getActivity());
        VodConfig.load(Config.vod(),  null);
        //mBinding.proxyText.setText(UrlUtil.scheme(proxy));
    }

    override fun getVersion(): String {
        return ""
    }

    override fun getCacheSize(): String {
        val size = getCacheSize(Path.cache())
         return byteCountToDisplaySize(size)
    }
    private fun getCacheSize(file: File): Long {
        var size: Long = 0
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                size += getCacheSize(it)
            }
        } else {
            size = file.length()
        }
        return size
    }

    private fun byteCountToDisplaySize(size: Long): String {
        if (size <= 0) return "0 KB"
        val units = arrayOf("bytes", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }

    override fun clearCache() {
        runBlocking (Dispatchers.IO){
            Path.clear(Path.cache())
            VodConfig.get().getConfig().json("").save()
        }
    }

    override fun hasCaption(): Boolean {
        return Intent(Settings.ACTION_CAPTIONING_SETTINGS).resolveActivity(
            appContext.packageManager
        ) != null
    }

    override fun isExoPlayer(): Boolean {
        return Setting.player == Players.EXO
    }

    override suspend fun setLanguage() {
        LanguageUtil.setLocale(LanguageUtil.getLocale(Setting.language))
        delay(1000)
        restartApp()
    }

    override fun resetApp() {
        Shell.exec("pm clear " +  appContext.packageName)
    }

    override fun getPref(key: String, default: Any): Any {
       return AndroidPref.get(key,default)
    }

    override fun setPref(key: String, value: Any) {
         return AndroidPref.put(key, value)
    }

    override fun getHostOkhttp(): Any {
       return  HostOkHttp.client()
    }
}

actual suspend fun FileDownloader.saveFile(channel: ByteReadChannel, savePath: String, totalSize: Long) {
    withContext(Dispatchers.IO) {
        val file = File(savePath)
        file.outputStream().use { outputStream ->
            var bytesCopied = 0L
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                val bytes = packet.readBytes()
                outputStream.write(bytes)
                bytesCopied += bytes.size

                // Calculate and report progress
                if (totalSize > 0) {
                    val progress = (bytesCopied * 100 / totalSize).toInt()
                    callback.onProgress(progress)
                }
            }
        }
    }
}

actual fun FileDownloader.savePath():String{
    return Path.cache("update.apk").absolutePath
}
private var androidPlatform:Platform?=null
actual fun getPlatform(): Platform  {
    if(androidPlatform==null){
        androidPlatform = AndroidPlatform()
    }
    return androidPlatform!!
}

actual class PlatformDecoder : AesDecoder by AndroidDecoder()

class AndroidUrlExtractor:UrlExtractor{
    override fun parse(flags: List<Flag>) {
        AndroidSource.get().parse(flags)
    }

    override fun fetch(result: Result): String {
        return AndroidSource.get().fetch(result)
    }

    override fun fetch(result: Channel): String {
        return AndroidSource.get().fetch(result)
    }

    override fun stop() {
        AndroidSource.get().stop()
    }

    override fun exit() {
        AndroidSource.get().exit()
    }

}

actual fun getUrlExtractor():UrlExtractor = AndroidUrlExtractor()

// androidMain
class AndroidDynamicLoader(private val dexClassLoader: DexClassLoader) : DynamicLoader {
    override fun loadClass(name: String): Any? =
        dexClassLoader.loadClass(name)

    override fun invoke(className: String, methodName: String, vararg args: Any?): Any? {
        val clazz = loadClass(className) as? Class<*> ?: throw ClassNotFoundException(className)
        val method = clazz.methods.find { it.name == methodName }
            ?: throw NoSuchMethodException("$className.$methodName")
        return method.invoke(null, *args)
    }
}

var appContext: Context = ContextProvider.context as Context

actual fun createDynamicLoader(jarPath: String): DynamicLoader {
    val optimizedDirectory = appContext.codeCacheDir.absolutePath
    val dexClassLoader = DexClassLoader(
        jarPath,
        optimizedDirectory,
        null,
        AndroidDynamicLoader::class.java.classLoader
    )
    return AndroidDynamicLoader(dexClassLoader)
}

actual object HashUtil {
    actual fun md5(src: String): String {
        return try {
            if (src.isEmpty()) return ""
            val digest = MessageDigest.getInstance("MD5")
            val bytes = digest.digest(src.toByteArray())
            val no = BigInteger(1, bytes)
            val sb = StringBuilder(no.toString(16))
            while (sb.length < 32) sb.insert(0, "0")
            sb.toString().lowercase(Locale.ROOT)
        } catch (e: Exception) {
            ""
        }
    }
}

actual object ContextProvider {
    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
        LanguageUtil.init(context as Application)
    }

    actual val context: Any
        get() = appContext
}

class AndroidSpiderLoader: SpiderLoader {

    private val jarLoader: com.calvin.box.movie.api.loader.JarLoader by lazy {
        com.calvin.box.movie.api.loader.JarLoader()
    }
   private  val pyLoader: PyLoader by lazy {
       PyLoader()
   }
   private  val jsLoader: JsLoader by lazy {
       JsLoader()
   }

    init {
        Init.set(ContextProvider.context as Context)
    }

    override suspend fun getSpider(site: Site): Any {
        val js: Boolean = site.api.contains(".js")
        val py: Boolean = site.api.contains(".py")
        val csp: Boolean = site.api.startsWith("csp_")
        return if (py) pyLoader.getSpider(site.key, site.api, site.ext)
        else if (js) jsLoader.getSpider(site.key, site.api, site.ext, site.jar)
        else if (csp) {
            var jar = site.jar
            if(jar.isEmpty()){
                jar = VodConfig.get().getHome()?.jar ?: ""
            }
            jarLoader.getSpider(site.key, site.api, site.ext, jar)
        }
        else SpiderNull()
    }

    override fun setRecent(site: Site) {
        val js: Boolean = site.api.contains(".js")
        val py: Boolean = site.api.contains(".py")
        val csp: Boolean = site.api.startsWith("csp_")
        if (js) jsLoader.setRecent(site.key)
        else if (py) pyLoader.setRecent(site.key)
        else if (csp) jarLoader.setRecent(site.jar)
    }

    override fun setRecent(jar: String) {
        jarLoader.parseJar(HashUtil.md5(jar), jar)
        jarLoader.setRecent(jar)
    }

    override suspend fun proxyLocal(params: Map<String, String>): Array<Any>? {
        Napier.d { "#proxyLocal,params: " }
        for (entity in params){
            Napier.d { "#proxyLocal, entity: key:${entity.key}, value:${entity.value} " }
        }
        return if ("js" == params["do"]) {
            jsLoader.proxyInvoke(params)
        } else if ("py" == params["do"]) {
            pyLoader.proxyInvoke(params)
        } else {
            jarLoader.proxyInvoke(params)
        }
    }

    override suspend fun clear() {
         jarLoader.clear()
        jsLoader.clear()
        pyLoader.clear()
    }

    override fun parseJar(key: String, jar: String) {
        jarLoader.parseJar(key, jar)
    }

    override fun writeWallPaper(localPath: String, url: String):Boolean {
        val file = File(localPath)
        return try {
            when {
                url.startsWith("file") -> Path.copy(Path.local(url), file)
                url.startsWith("assets") -> Path.copy(Asset.open(url), file)
                url.startsWith("http") -> Path.write(file, HostOkHttp.newCall(url).execute().body!!.bytes())
                else -> {Napier.d(tag = "wallPager"){"error wallPaper url"}}
            }
           // resize(file)
            file.exists() && file.length()>0
        } catch (e: Exception) {
            file.exists() && file.length()>0
        }
    }

    override suspend fun loadHomeContent(site:Site): Result {
            if (site.type == 3) {
                val spider: Spider =  getSpider(site) as Spider
                Napier.d { "#loadHomeContent, spider: $spider" }
                val homeContent: String = spider.homeContent(true)
                //SpiderDebug.log(homeContent)
                Napier.d { "#loadHomeContent, homeContent:type3 $homeContent" }
                setRecent(site)
                val result: Result = Result.fromJson(homeContent)
                if (result.list.isNotEmpty()) return result
                val homeVideoContent: String = spider.homeVideoContent()
                //SpiderDebug.log(homeVideoContent)
                Napier.d { "#loadHomeContent, homeVideoContent:type3 $homeVideoContent" }
                result.list = (Result.fromJson(homeVideoContent).list)
                return result
            } else if (site.type== 4) {
                val params: ArrayMap<String, String> =
                    ArrayMap<String, String>()
                params["filter"] = "true"
                val homeContent: String = call(site, params, false)
                SpiderDebug.log(homeContent)
                return Result.fromJson(homeContent)
            } else {
                val homeContent: String =
                    try {
                        HostOkHttp.newCall(site.api, getOKhttpHeaders(site.header)).execute()
                            .body?.string() ?:""
                    } catch (e: Exception) {
                         e.printStackTrace()
                        "{}"
                    }
                SpiderDebug.log(homeContent)
                return fetchPic(site, Result.fromType(site.type, homeContent))
            }


    }

    override suspend fun loadCategoryContent(
        site: Site,
        category: com.calvin.box.movie.bean.Class,
        page: String,
        filter: Boolean,
    ):Result {
        val extend: HashMap<String, String>  = category.getExtend(false)
        if (site.type == 3) {
            val spider: Spider = getSpider(site) as Spider
           // Napier.d { "#loadCategory, spider: $spider" }
            val categoryContent = spider.categoryContent(category.typeId, page, filter, extend)
            //SpiderDebug.log(categoryContent)
            Napier.d { "#loadcategoryContent, categoryContent:type3, classType: ${category.typeId},json:  $categoryContent" }
            setRecent(site)
            return Result.fromJson(categoryContent)
        } else {
            val params = ArrayMap<String, String>()
            if (site.type == 1 && extend.isNotEmpty()) params["f"] = Json.encodeToString(extend)
            if (site.type == 4) params["ext"] =
                HostUtil.base64(Json.encodeToString(extend), HostUtil.URL_SAFE)
            params["ac"] = if (site.type == 0) "videolist" else "detail"
            params["t"] = category.typeId
            params["pg"] = page
            val categoryContent = call(site, params, true)
            Napier.d { "#loadcategoryContent, categoryContent:type_99 $categoryContent" }
           // SpiderDebug.log(categoryContent)
            return Result.fromType(site.type, categoryContent)
        }
    }

    var lastCallTime:Long = 0

    override suspend fun loadDetailContent(site: Site, vodId: String): Result {
        lastCallTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        if (site.type == 3) {
            val spider: Spider = getSpider(site) as Spider
            val detailContent =
                try {
                    spider.detailContent(listOf(vodId))
                } catch (e: Exception) {
                    e.printStackTrace()
                    "{}"
                }

            SpiderDebug.log("#loadDetailContent: type3 $detailContent")
             setRecent(site)
            val result = Result.fromJson(detailContent)
            if (result.list.isNotEmpty()) result.list[0].setVodFlags()
            if (result.list.isNotEmpty()) Source.parse(result.list[0].vodFlags)
            return result
        } else if (site.isEmpty() && "push_agent" == site.key) {
            val vod = Vod()
            vod.vodId = vodId
            vod.vodName = vodId
            vod.vodPic = pushImage//(ResUtil.getString(R.string.push_image))
            vod.vodFlags =
                Flag.create(
                    "Push",//ResUtil.getString(R.string.push),
                    "Play",//ResUtil.getString(R.string.play),
                    vodId
                )
            Source.parse(vod.vodFlags)
            return Result.vod(vod)
        } else {
            val params = ArrayMap<String, String>()
            params["ac"] = if (site.type == 0) "videolist" else "detail"
            params["ids"] = vodId
            val detailContent = call(site, params, true)
            SpiderDebug.log("#loadDetailContent: type99 $detailContent")
            val result = Result.fromType(site.type, detailContent)
            if (result.list.isNotEmpty()) result.list[0].setVodFlags()
            if (result.list.isNotEmpty()) Source.parse(result.list[0].vodFlags)
            return result
        }
    }

    override suspend fun loadPlayerContent(site: Site, vodId:String, vodFlag: String): Result {
        val id = vodId
        val flag = vodFlag
        val key = site.key
        Source.stop()
        if (site.type == 3) {
            val spider: Spider =  getSpider(site) as Spider
            val playerContent =
                try {
                    spider.playerContent(flag, id, VodConfig.get().getFlags())
                } catch (e: Exception) {
                    e.printStackTrace()
                    "{}"
                }

            SpiderDebug.log("#loadPlayerContent: type3 $playerContent")
             setRecent(site)
            val result = Result.fromJson(playerContent)
            if (result.flag.isEmpty()) result.flag = vodFlag
            val fetchedUrl = Source.fetch(result)
            SpiderDebug.log("#loadPlayerContent: fetchedUrl: $fetchedUrl")
            result.setUrl(fetchedUrl)
            if(result.header.isNullOrEmpty()) result.header = site.header.toString()
            result.key = key
            return result
        } else if (site.type == 4) {
            val params = ArrayMap<String, String>()
            params["play"] = id
            params["flag"] = flag
            val playerContent = call(site, params, true)
            SpiderDebug.log("#loadPlayerContent: type4 $playerContent")
            val result = Result.fromJson(playerContent)
            if (result.flag.isEmpty()) result.flag = (flag)
            result.setUrl(Source.fetch(result))
            result.header = (site.header).toString()
            return result
        } else if (site.isEmpty() && "push_agent" == key) {
            val result = Result()
            result.parse = 0
            result.flag = flag
            result.url = (Url.create().add(id))
            result.setUrl(Source.fetch(result))
            return result
        } else {
            var url: Url? = Url.create().add(id)
            val type = Uri.parse(id).getQueryParameter("type")
            if ("json" == type) {
                val result = HostOkHttp.newCall(id, getOKhttpHeaders(site.header)).execute().body?.let {
                    Result.fromJson(
                        it.string())
                }
                url = result?.url
            }

            val result = Result()
            result.url = url?:Url()
            result.flag = flag
            result.header = (site.header).toString()
            result.playUrl = site.playUrl
            result.setUrl(Source.fetch(result))
            result.parse = (
                if (Sniffer.isVideoFormat(url?.v()) && result.playUrl.isEmpty()) 0 else 1
            )
            SpiderDebug.log(result.toString())
            return result
        }
    }

    override suspend fun loadSearchContent(
        site: Site,
        keyword: String,
        quick: Boolean,
        page: String
    ): Result {
        Napier.d { "#loadSearchContent,request params site: $site, keyword: $keyword, quick: $quick, page: $page" }
        if (site.type == 3) {
            val spider: Spider = getSpider(site) as Spider
            val transKey = Trans.t2s(keyword)
            //Napier.d { "#loadSearchContent, type3 spider: $spider, trans keyword: $transKey" }
            val searchContent =
                try {
                    spider.searchContent(transKey, quick, page)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ""
                }

            SpiderDebug.log("#loadSearchContent，response type3 spider: $spider, trans keyword: $transKey，site: ${site.name}, json: $searchContent ")
            val result = Result.fromJson(searchContent)
            for (vod in result.list) vod.site = site
            return result
        } else {
            val params = ArrayMap<String, String>()
            params["wd"] = Trans.t2s(keyword)
            params["pg"] = page
            val searchContent = call(site, params, true)
            SpiderDebug.log("#loadSearchContent,type4, site:  ${site.name}, json: $searchContent")
            val result = fetchPic(site, Result.fromType(site.type, searchContent))
            for (vod in result.list) vod.site = site
            return result
        }
    }


    private fun getOKhttpHeaders(header: JsonElement?): Headers {
        val headersBuilder = Headers.Builder()
        header?.jsonObject?.forEach { (key, value) ->
            headersBuilder.add(key, value.jsonPrimitive.content)
        }
        return headersBuilder.build()
    }

    @Throws(IOException::class)
    private fun call(site: Site, params: ArrayMap<String, String>, limit: Boolean): String {
        try {
            val call = if (fetchExt(site, params, limit).length <= 1000) HostOkHttp.newCall(
                site.api,
                getOKhttpHeaders(site.header),
                params
            ) else HostOkHttp.newCall(site.api, getOKhttpHeaders(site.header), HostOkHttp.toBody(params))
            return call.execute().body!!.string()
        } catch (e: Exception) {
           e.printStackTrace()
            return "{}"
        }
    }

    @Throws(java.lang.Exception::class)
    private fun fetchPic(site: Site, result: Result): Result {
        try {
            if (site.type > 2 || result.list.isEmpty() || result.list[0].vodPic.isNotEmpty()
            ) return result
            val ids = ArrayList<String?>()
            if (site.categories.isEmpty()) for (item in result.list) ids.add(item.vodId.toString())
            else for (item in result.list) if (site.categories
                    .contains(item.typeName)
            ) ids.add(item.vodId.toString())
            if (ids.isEmpty()) return result.clear()
            val params = ArrayMap<String, String>()
            params["ac"] = if (site.type == 0) "videolist" else "detail"
            params["ids"] = TextUtils.join(",", ids)
            val body = HostOkHttp.newCall(site.api, getOKhttpHeaders(site.header), params).execute().body
            val response: String = body?.string() ?:""
            body?.close()
            result.list = (Result.fromType(site.type, response).list )
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            return result
        }
    }

    @Throws(IOException::class)
    private fun fetchExt(site: Site, params: ArrayMap<String, String>, limit: Boolean): String {
        var extend: String = site.ext
        if (extend.startsWith("http")) extend = fetchExt(site)
        if (limit && extend.length > 1000) extend = extend.substring(0, 1000)
        if (extend.isNotEmpty()) params["extend"] = extend
        return extend
    }
    @Throws(IOException::class)
    private fun fetchExt(site: Site): String {
        val res: Response = HostOkHttp.newCall(site.ext, getOKhttpHeaders(site.header)).execute()
        if (res.code != 200) return ""
        site.ext = (res.body!!.string())
        return site.ext
    }

    /*private fun resize(file: File): File {
        return try {
            val bitmap = Glide.with(App.get())
                .asBitmap()
                .load(file)
                .centerCrop()
                .override(ResUtil.getScreenWidth(), ResUtil.getScreenHeight())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .submit()
                .get()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
            bitmap.recycle()
            file
        } catch (e: Exception) {
            file
        }
    }*/
}


actual fun getSpiderLoader(): SpiderLoader  {
    if(spiderLoader == null){
        spiderLoader = AndroidSpiderLoader()
    }
    return spiderLoader!!
}


class AndroidDataFactory:DataFactory{
    override fun createRoomDatabase(): MoiveDatabase {
        val dbFile = appContext.getDatabasePath(MoiveDatabase.NAME)
        return Room.databaseBuilder<MoiveDatabase>(appContext, dbFile.absolutePath)
            .addMoiveMigrations()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    override fun createApi() = commonBuildApi()

    override fun createDataStore(): DataStore<Preferences> {
        return  getMoiveDataStore(
            producePath = { appContext.filesDir.resolve(moiveDsFileName).absolutePath }
        )
    }

}
actual fun getDataFactory():DataFactory = AndroidDataFactory()

private val pushImage:String = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT/2wBDAQMEBAUEBQkFBQkUDQsNFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBT/wAARCADwAPADASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACvN7bxhffFCaRPB+oWZ8KSQX1h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g74M+K1mbfxR4fs9UYLtS5ZNlxH/ALkq4dfoDitbp7mFmtjGvNNudPbbcQtH6NjIP41518fo5NY8G+FfAFsxS88da3DaTbSQy2ULCWdwRzwFX8GNTyfBX4nfCdd/w38bf8JHoqf8yx4w/ertzkrFcDkdwAQoHcml+F9n4v8AiP8AHCLxZ4w8H3Xg6Hwxop020sZ3WSF7yaQmWWGQcOvlhVyOOcZOKaVtbkt30seofFbVo/C/gCS2s9Y07w3dXTRabp9xqUBmt/NcgLEYwDuDKGXFdhptmun6fa2qrGiwRLEFhQRoAoAwqjhRxwB0p11ZW98ipcwRXCK6yKsqBgGByGGe4PQ1PUFhRRRSGFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAVhePP+RH8Rf9g64/9FNW7WD483HwN4i2RvK/9nXOI413Mx8puAO5NNbgfB/jX4kWV58A/hN8NZdQaw0u40+PVvEl1Dy0NjHI2yMdi7sPlU9XEQ/ir6Q+DPw1vPGOt6f8RfGGljTBZ262/hTwww/d6LZgAK7L/wA92ULk/wAIAHYBfFf2N/2XJtelsfiN47tWeKIRjR9LuoyN4jUIk8in+EBRsB643dMZ+6K1m0tEYwTerCiiisTYKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAP/Z"

class AndroidNanoServer: NanoServer{
    override fun start() {
         Server.get().start()
    }

    override fun stop() {
        Server.get().stop()
    }
}

actual fun getNanoServer():NanoServer  = AndroidNanoServer()

actual fun okhttpSetup(pref:BasePreference){
    val proxy = AndroidPref.get("proxy","") as String
    Napier.d { "set okhttp proxy: $proxy" }
    HostOkHttp.get().setProxy(proxy)

    /*val doh =  runBlocking { pref.doh.get() }
    OkHttp.get().setDoh( Doh.objectFrom(doh))*/
}