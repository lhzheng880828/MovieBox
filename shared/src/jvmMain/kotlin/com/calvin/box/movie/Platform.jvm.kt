package com.calvin.box.movie

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.calvin.box.movie.bean.Channel
import com.calvin.box.movie.bean.Doh
import com.calvin.box.movie.bean.Flag
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.db.MoiveDatabase
import com.calvin.box.movie.db.addMoiveMigrations
import com.calvin.box.movie.di.getDataStore
import com.calvin.box.movie.pref.BasePreference
import com.calvin.box.movie.utils.FileDownloader
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLClassLoader
import java.io.File

import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override fun getDefaultCountry(): String {
        return java.util.Locale.getDefault().country
    }

    override fun getPlatformRegex(pattern: String): Regex {
        return Regex(pattern)
    }

    override fun getUriQuery(url: String): String {
        return Uri(url).query
    }

    override fun url2FileName(url: String): String {
        return if (url.startsWith("file")) {
            File(url).name
        } else {
            Uri(url).path ?: ""
        }
    }

    override fun writeStringToFile(fileName: String, content: String) {
        TODO("Not yet implemented")
    }

    override fun setDoh(doh: Doh) {
        TODO("Not yet implemented")
    }

    override fun setProxy(proxy: String) {
        TODO("Not yet implemented")
    }

    override fun getVersion(): String {
        TODO("Not yet implemented")
    }

    override fun getCacheSize(): String {
        TODO("Not yet implemented")
    }

    override fun clearCache() {
        TODO("Not yet implemented")
    }

    override fun hasCaption(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isExoPlayer(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setLanguage() {
        TODO("Not yet implemented")
    }

    override fun resetApp() {
        TODO("Not yet implemented")
    }

    override fun setPref(key: String, value: Any) {
        TODO("Not yet implemented")
    }

    override fun getPref(key: String, default: Any): Any {
        TODO("Not yet implemented")
    }

    override fun getHostOkhttp(): Any {
        TODO("Not yet implemented")
    }

    override fun navigateToLiveScreen(className: String) {
        TODO("Not yet implemented")
    }
}

actual fun getPlatform(): Platform = JVMPlatform()

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
    return  ""
}

actual class PlatformDecoder : AesDecoder by JvmDecoder()
// jvmMain

class JvmUrlExtractor:UrlExtractor{
    override fun parse(flags: List<Flag>) {
    }

    override fun fetch(result: Result): String {
        return  ""
    }

    override fun fetch(result: Channel): String {
        return ""
    }

    override fun stop() {

    }

    override fun exit() {

    }

}

actual fun getUrlExtractor():UrlExtractor = JvmUrlExtractor()

class JvmDynamicLoader(private val urlClassLoader: URLClassLoader) : DynamicLoader {
    override fun loadClass(name: String): Any? =
        urlClassLoader.loadClass(name)

    override fun invoke(className: String, methodName: String, vararg args: Any?): Any? {
        val clazz = loadClass(className) as? Class<*> ?: throw ClassNotFoundException(className)
        val method = clazz.methods.find { it.name == methodName }
            ?: throw NoSuchMethodException("$className.$methodName")
        return method.invoke(null, *args)
    }
}

actual fun createDynamicLoader(jarPath: String): DynamicLoader {
    val jarFile = File(jarPath)
    val urlClassLoader = URLClassLoader(arrayOf(jarFile.toURI().toURL()), JvmDynamicLoader::class.java.classLoader)
    return JvmDynamicLoader(urlClassLoader)
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

    actual val context: Any
        get() = ""
}

class JvmSpiderLoader: SpiderLoader {
    override suspend fun getSpider(site: Site): Any {
        TODO("Not yet implemented")
    }

    override fun setRecent(site: Site) {
        TODO("Not yet implemented")
    }

    override fun setRecent(jar: String) {
        TODO("Not yet implemented")
    }

    override suspend fun proxyLocal(params: Map<String, String>): Array<Any>? {
        TODO("Not yet implemented")
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
    }

    override fun writeWallPaper(localPath: String, url: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun parseJar(key: String, jar: String) {
        TODO("Not yet implemented")
    }

    override suspend fun loadHomeContent(site: Site): Result {
        TODO("Not yet implemented")
    }

    override suspend fun loadCategoryContent(
        homeSite: Site,
        categoryType: String,
        categoryExt: HashMap<String, String>,
        page: String,
        filter: Boolean,
    ):Result {
        TODO("Not yet implemented")
    }

    override suspend fun loadDetailContent(
        homeSite: Site,
        vodId: String,
    ): Result {
        TODO("Not yet implemented")
    }

    override suspend fun loadPlayerContent(site: Site, vodId: String, vodFlag: String): Result {
        TODO("Not yet implemented")
    }

    override suspend fun loadSearchContent(
        site: Site,
        keyword: String,
        quick: Boolean,
        page: String
    ): Result {
        TODO("Not yet implemented")
    }
}
actual fun getSpiderLoader(): SpiderLoader = JvmSpiderLoader()


class JvmDataFactory:DataFactory{
    override fun createRoomDatabase(): MoiveDatabase {
        val dbFile = File(System.getProperty("java.io.tmpdir"), MoiveDatabase.NAME)
        return Room.databaseBuilder<MoiveDatabase>(name = dbFile.absolutePath)
            .addMoiveMigrations()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    override fun createApi() = commonBuildApi()

    override fun createDataStore(): DataStore<Preferences> {
        val pref = File(System.getProperty("java.io.tmpdir"), moiveDsFileName)
        return  getDataStore {
            pref.absolutePath
        }
    }

}

actual fun getDataFactory():DataFactory = JvmDataFactory()


class JvmNanoServer:NanoServer{
    override fun start() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}

actual fun getNanoServer():NanoServer  = JvmNanoServer()

actual fun okhttpSetup(pref: BasePreference){

}