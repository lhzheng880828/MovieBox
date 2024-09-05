package com.calvin.box.movie

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.calvin.box.movie.bean.Channel
import com.calvin.box.movie.bean.Class
import com.calvin.box.movie.bean.Doh
import com.calvin.box.movie.bean.Flag
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.db.MoiveDatabase
import com.calvin.box.movie.network.MoiveApi
import com.calvin.box.movie.network.MoiveNetworkApi
import com.calvin.box.movie.pref.BasePreference
import com.calvin.box.movie.utils.FileDownloader
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.ByteReadChannel
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath

interface Platform {
    val name: String
    fun getDefaultCountry(): String

    fun getPlatformRegex(pattern: String): Regex

    fun getUriQuery(url:String):String?

    fun url2FileName(url: String):String

    fun writeStringToFile(fileName: String, content: String)

    fun setDoh(doh: Doh)

    fun setProxy(proxy: String)

    fun getVersion():String

    fun getCacheSize():String

    fun clearCache()


}

expect fun getPlatform(): Platform

interface AesDecoder {
    fun ecbDecrypt(data: String, key: String): String
    fun cbcDecrypt(data: String, key: String, iv: String): String
    fun getSpider(url: String): Any?
    fun loadJsonData(url: String): String
}

expect class PlatformDecoder() : AesDecoder


interface UrlExtractor {

    fun parse(flags: List<Flag>)
    fun fetch(result: Result):String
    fun fetch(result: Channel):String
    fun stop()
    fun exit()
}
expect fun getUrlExtractor(): UrlExtractor

// commonMain
interface DynamicLoader {
    fun loadClass(name: String): Any?
    fun invoke(className: String, methodName: String, vararg args: Any?): Any?
}

expect fun createDynamicLoader(jarPath: String): DynamicLoader

// commonMain
class JarLoader(private val jarPath: String) {
    private val loader by lazy { createDynamicLoader(jarPath) }

    fun loadClass(name: String): Any? = loader.loadClass(name)

    fun invoke(className: String, methodName: String, vararg args: Any?): Any? {
        return loader.invoke(className, methodName, *args)
    }
}


// commonMain
/*
class SomeSharedClass {
    fun loadAndUseJar() {
        val jarLoader = JarLoader("/path/to/your.jar")
        val result = jarLoader.invoke("com.example.SomeClass", "someMethod", arg1, arg2)

    }
}*/

expect object HashUtil {
    fun md5(src: String): String
}

expect object ContextProvider {
    val context: Any // This can be any type, but typically Android's Context type
}

interface SpiderLoader{
   suspend fun getSpider(site: Site): Any
    fun setRecent(site: Site)
    fun setRecent(jar: String)
    suspend fun proxyLocal(params: Map<String, String>): Array<Any>?
    fun parseJar(key: String, jar: String)
    suspend fun clear()
    fun writeWallPaper(localPath:String, url:String):Boolean

    suspend fun loadHomeContent(site:Site):Result

    suspend fun loadCategoryContent(site:Site, category:Class, page:String = "1", filter:Boolean = true):Result

    suspend fun loadDetailContent(site:Site, vodId:String):Result

    suspend fun loadPlayerContent(site: Site, vodId:String, vodFlag: String):Result

    suspend fun loadSearchContent(site: Site, keyword:String, quick:Boolean, page: String):Result

}

var spiderLoader: SpiderLoader? = null

expect fun getSpiderLoader(): SpiderLoader


interface NanoServer {
    fun start()
    fun stop()
}

expect fun getNanoServer(): NanoServer

expect fun okhttpSetup(pref:BasePreference)


 interface DataFactory {
    fun createRoomDatabase(): MoiveDatabase

    fun createApi(): MoiveApi

    fun  createDataStore(): DataStore<Preferences>

}

expect fun getDataFactory():DataFactory

internal fun commonBuildApi(): MoiveApi = MoiveNetworkApi(
    client = HttpClient {
        install(ContentNegotiation) {
            json(json, contentType = ContentType.Any)
        }
    },
    apiUrl = "https://yenerm.github.io/frutties/",
)
val json = Json { ignoreUnknownKeys = true }



private lateinit var dataStore: DataStore<Preferences>

private val lock = SynchronizedObject()

/**
 * Gets the singleton DataStore instance, creating it if necessary.
 */
fun getMoiveDataStore(producePath: () -> String): DataStore<Preferences> =
    synchronized(lock) {
        if (::dataStore.isInitialized) {
            dataStore
        } else {
            PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
                .also { dataStore = it }
        }
    }

internal const val moiveDsFileName = "moive.preferences_pb"
expect suspend fun FileDownloader.saveFile(
    channel: ByteReadChannel,
    savePath: String,
    totalSize: Long
)

expect fun FileDownloader.savePath():String