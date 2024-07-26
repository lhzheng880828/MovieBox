package com.calvin.box.movie.api.loader


import com.calvin.box.movie.ContextProvider
import com.calvin.box.movie.PlatformDecoder
import android.content.Context
import com.calvin.box.movie.utils.UrlUtil
import com.github.catvod.crawler.Spider
import com.github.catvod.crawler.SpiderNull
import com.github.catvod.net.OkHttp
import com.github.catvod.utils.Path
import com.github.catvod.utils.Util
import dalvik.system.DexClassLoader
import io.github.aakira.napier.Napier
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import java.io.File
import java.lang.reflect.Method

/*expect class DexClassLoader
expect class Context*/

class JarLoader {
    private val loaders = mutableMapOf<String, DexClassLoader>()
    private val methods = mutableMapOf<String, Method>()
    private val spiders = mutableMapOf<String, Spider>()
    private val mutex = Mutex()
    private var recent: String = ""
    private val decoder = PlatformDecoder()

    suspend fun clear() = mutex.withLock {
        spiders.values.forEach { it.destroy() }
        loaders.clear()
        methods.clear()
        spiders.clear()
    }

    fun setRecent(recent: String) {
        this.recent = recent
    }

    private fun load(key: String, file: File)  {
        Napier.d{"#load Dex method call, key: $key, file path: ${file.absolutePath}"}
        loaders[key] = DexClassLoader(file.absolutePath, Path.jar().absolutePath, null, (ContextProvider.context as Context).classLoader)
        invokeInit(key)
        putProxy(key)
    }

    private fun invokeInit(key: String) {
        try {
            val clz = loaders[key]?.loadClass("com.github.catvod.spider.Init")
            val method = clz?.getMethod("init", Context::class.java)
            method?.invoke(clz, (ContextProvider.context as Context))
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun putProxy(key: String) {
        try {
            val clz = loaders[key]?.loadClass("com.github.catvod.spider.Proxy")
            val method = clz?.getMethod("proxy", Map::class.java)
            method?.let { methods[key] = it }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private  fun download(url: String): File {
        return try {
            Path.write(Path.jar(url), OkHttp.newCall(url).execute().body?.bytes() ?: byteArrayOf())
        } catch (e: Exception) {
            Path.jar(url)
        }
    }

     fun parseJar(key: String, spiderStr: String)  {
         Napier.d{"#parseJar, key: $key, spider: $spiderStr"}
        if (key in loaders) return
        val (jarUrl, md5) = spiderStr.split(";md5;").let {
            it[0] to (it.getOrNull(1)?.trim() ?: "")
        }
         Napier.d{"#parseJar, jarUrl: $jarUrl, md5: $md5"}
         var jarKey = key
         if(key.isEmpty()) jarKey = Util.md5(spiderStr)
        when {
            md5.isNotEmpty() && Util.equals(jarUrl, md5) -> load(jarKey, Path.jar(jarUrl))
            jarUrl.startsWith("img+") -> load(jarKey, (decoder.getSpider(jarUrl) as File))
            jarUrl.startsWith("http") -> load(jarKey, download(jarUrl))
            jarUrl.startsWith("file") -> load(jarKey, Path.local(jarUrl))
            jarUrl.startsWith("assets") -> parseJar(jarKey, UrlUtil.convert(jarUrl))
            jarUrl.isNotEmpty() -> parseJar(jarKey, UrlUtil.convert(jarUrl))
        }
    }

    suspend fun getLoader(key: String, jar: String): DexClassLoader? = mutex.withLock {
        if (key !in loaders) parseJar(key, jar)
        return loaders[key]
    }

    suspend fun getSpider(key: String, api: String, ext: String, spiderStr: String): Spider = mutex.withLock {
        Napier.d { "#getSpider, key:$key, api:$api, ext:$ext, spiderStr:$spiderStr" }
        val jaKey = Util.md5(spiderStr)
        val spKey = jaKey + key
        Napier.d { "#getSpider, jarKey:$jaKey, spKey:$spKey" }
        return spiders.getOrPut(spKey) {
            try {
                if (jaKey !in loaders) parseJar(jaKey, spiderStr)
                val spider = loaders[jaKey]?.loadClass("com.github.catvod.spider.${api.split("csp_")[1]}")
                    ?.getDeclaredConstructor()?.newInstance() as? Spider
                spider?.init( ContextProvider.context as Context, ext)
                spider ?: SpiderNull()
            } catch (e: Throwable) {
                e.printStackTrace()
                SpiderNull()
            }
        }
    }

    suspend fun jsonExt(key: String, jxs: Map<String, String>, url: String): JSONObject = mutex.withLock {
        val clz = loaders[""]?.loadClass("com.github.catvod.parser.Json$key")
        val method = clz?.getMethod("parse", Map::class.java, String::class.java)
        return method?.invoke(null, jxs, url) as? JSONObject ?: JSONObject()
    }

    suspend fun jsonExtMix(flag: String, key: String, name: String, jxs: Map<String, Map<String, String>>, url: String): JSONObject = mutex.withLock {
        val clz = loaders[""]?.loadClass("com.github.catvod.parser.Mix$key")
        val method = clz?.getMethod("parse", Map::class.java, String::class.java, String::class.java, String::class.java)
        return method?.invoke(null, jxs, name, flag, url) as? JSONObject ?: JSONObject()
    }

    suspend fun proxyInvoke(params: Map<String, String>): Array<Any>? = mutex.withLock {
        try {
            val method = methods[Util.md5(recent)]
            return method?.invoke(null, params) as? Array<Any>
        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        }
    }

   /* companion object {
        expect fun createDexClassLoader(dexPath: String, optimizedDirectory: String, librarySearchPath: String?, parent: ClassLoader?): DexClassLoader
        expect fun getAppClassLoader(): ClassLoader
        expect fun getAppContext(): Context
    }*/
}