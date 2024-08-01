package com.calvin.box.movie

import Decoder
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.calvin.box.movie.bean.Class
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.database.dbFileName
import com.calvin.box.movie.db.MoiveDatabase
import com.calvin.box.movie.db.addMoiveMigrations
import platform.UIKit.UIDevice
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.NSString
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURL

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.length
import platform.Foundation.md5
import platform.Foundation.toByteArray


class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override fun getDefaultCountry(): String {
        return NSLocale.currentLocale.countryCode ?: ""
    }

    override fun getPlatformRegex(pattern: String): Regex {
        return Regex(pattern)
    }

    override fun getUriQuery(url: String): String? {
        val nsUrl = NSURL.URLWithString(url)
        val components = NSURLComponents(nsUrl, true)
        return components?.query
    }

    override fun url2FileName(url: String): String {
        TODO("Not yet implemented")
    }


}

actual fun getPlatform(): Platform = IOSPlatform()

actual class PlatformDecoder : Decoder by IosDecoder()

// iosMain, macosMain, etc.
class UnsupportedDynamicLoader : DynamicLoader {
    override fun loadClass(name: String): Any? = null

    override fun invoke(className: String, methodName: String, vararg args: Any?): Any? = null
}

actual fun createDynamicLoader(jarPath: String): DynamicLoader = UnsupportedDynamicLoader()

actual object HashUtil {
    actual fun md5(src: String): String {
        if (src.isEmpty()) return ""
        val data = NSString.create(string = src).dataUsingEncoding(NSUTF8StringEncoding)
        return data?.md5()?.toHexString() ?: ""
    }

    private fun NSData.md5(): NSData {
        val digestLength = 16 // MD5 Digest length
        val buffer = ByteArray(digestLength)
        usePinned {
            CC_MD5(this@md5.bytes, this@md5.length.convert(), buffer.refTo(0))
        }
        return NSData.create(bytes = buffer.refTo(0).addressOf(0), length = digestLength.convert())
    }

    private fun NSData.toHexString(): String {
        val hexDigits = "0123456789abcdef"
        val result = StringBuilder(length.toInt() * 2)
        val byteArray = toByteArray()
        for (byte in byteArray) {
            val i = byte.toInt()
            result.append(hexDigits[i shr 4 and 0x0f])
            result.append(hexDigits[i and 0x0f])
        }
        return result.toString()
    }
}
actual object ContextProvider {
    actual val context: Any
        get() = Unit // Or provide actual iOS-specific context if needed
}

class IosSpiderLoader: SpiderLoader {
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
        category: Class,
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
actual fun getSpiderLoader(): SpiderLoader = IosSpiderLoader()


class IosDataFactory:DataFactory{
    override fun createRoomDatabase(): MoiveDatabase {
        val dbFile = "${fileDirectory()}/$dbFileName"
        return Room.databaseBuilder<MoiveDatabase>(
            name = dbFile,
            factory =  { MoiveDatabase::class.instantiateImpl() })
            .addMoiveMigrations()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    private fun fileDirectory(): String {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory).path!!
    }

    override fun createApi() = commonBuildApi()

    override fun createDataStore(): DataStore<Preferences> {
        return getMoiveDataStore(
            producePath = {
                val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )
                requireNotNull(documentDirectory).path + "/$moiveDsFileName"
            }
        )
    }

}
actual fun getDataFactory():DataFactory = IosDataFactory()


class IosNanoServer:NanoServer{
    override fun start() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}

actual fun getNanoServer():NanoServer  = IosNanoServer()