package com.calvin.box.movie.api.loader

import com.calvin.box.movie.ContextProvider
import android.content.Context
import com.calvin.box.movie.api.config.VodConfig
import com.calvin.box.movie.getSpiderLoader
import com.github.catvod.crawler.Spider
import com.github.catvod.crawler.SpiderNull
import dalvik.system.DexClassLoader
import io.github.aakira.napier.Napier
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


class JsLoader {
    private val spiders = mutableMapOf<String, Spider>()
    private val jarLoader = JarLoader()
    private val mutex = Mutex()
    private var recent: String = ""

    suspend fun clear() = mutex.withLock {
        spiders.values.forEach { it.destroy() }
        jarLoader.clear()
        spiders.clear()
    }

    fun setRecent(recent: String) {
        Napier.d{"#setRecent, recent: $recent"}
        this.recent = recent
    }

    private suspend fun dex(key: String, jar: String): DexClassLoader? {
        return try {
            if (jar.isEmpty()) null else jarLoader.getLoader(key, jar)
        } catch (e: Throwable) {
            null
        }
    }

    suspend fun getSpider(key: String, api: String, ext: String, jar: String): Spider = mutex.withLock {
        return spiders.getOrPut(key) {
            try {
                val spider = com.fongmi.quickjs.crawler.Spider(key, api, dex(key, jar))
                spider.init( ContextProvider.context as Context, ext)
                spider
            } catch (e: Throwable) {
                e.printStackTrace()
                SpiderNull()
            }
        }
    }

    private suspend fun find(params: Map<String, String>): Spider = mutex.withLock {
        val siteKey = params["siteKey"]
        Napier.d{"#find, siteKey: $siteKey"}
        return if (siteKey == null) {
            spiders[recent] ?: SpiderNull()
        } else {
            val site = VodConfig.get().getSite(siteKey)
              val spiderLoader =  getSpiderLoader()
            if (site.isEmpty()) SpiderNull() else spiderLoader.getSpider(site) as Spider
        }
    }

    suspend fun proxyInvoke(params: Map<String, String>): Array<Any>? {
        return try {
            find(params).proxyLocal(params)
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }


}