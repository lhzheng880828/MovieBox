package com.calvin.box.movie.api.loader

import com.calvin.box.movie.ContextProvider
import android.content.Context
import com.calvin.box.movie.api.config.VodConfig
import com.calvin.box.movie.getSpiderLoader
import com.github.catvod.crawler.Spider
import com.github.catvod.crawler.SpiderNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

//expect class Context

class PyLoader {
    private val spiders = mutableMapOf<String, Spider>()
    private val mutex = Mutex()
    private var loader: Any? = null
    private var recent: String = ""

    init {
        initLoader()
    }

    suspend fun clear() = mutex.withLock {
        spiders.values.forEach { it.destroy() }
        spiders.clear()
    }

    fun setRecent(recent: String) {
        this.recent = recent
    }

    private fun initLoader() {
        try {
            val loaderClass = Class.forName("com.undcover.freedom.pyramid.Loader")
            loader = loaderClass.newInstance()
        } catch (e: Throwable) {
            // Ignore the exception
        }
    }

    suspend fun getSpider(key: String, api: String, ext: String): Spider = mutex.withLock {
        return spiders.getOrPut(key) {
            try {
                val method = loader?.javaClass?.getMethod("spider", Context::class.java, String::class.java, String::class.java)
                val spider = method?.invoke(loader, ContextProvider.context, key, api) as? Spider
                spider?.init(ContextProvider.context as Context, ext)
                spider ?: SpiderNull()
            } catch (e: Throwable) {
                e.printStackTrace()
                SpiderNull()
            }
        }
    }

    private suspend fun find(params: Map<String, String>): Spider = mutex.withLock {
        val siteKey = params["siteKey"]
        return if (siteKey == null) {
            spiders[recent] ?: SpiderNull()
        } else {
            val site = VodConfig.get().getSite(siteKey)
            if (site.isEmpty()) SpiderNull() else  getSpiderLoader().getSpider(site) as Spider
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

   /* companion object {
        expect fun getAppContext(): Context
    }*/
}