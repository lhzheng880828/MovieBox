package com.calvin.box.movie.player.exo

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.calvin.box.movie.ContextProvider
import java.io.File
import kotlin.concurrent.Volatile

@UnstableApi
class CacheManager {
    private var cache: SimpleCache? = null

    private object Loader {
        @Volatile
        var INSTANCE: CacheManager = CacheManager()
    }

    fun getCache(): Cache {
        if (cache == null) create()
        return cache!!
    }

    private fun create() {
        val context = ContextProvider.context as Context
        val exo =  mkdir(
            File(
                context.cacheDir.absolutePath + File.separator + "exo"
            )
        )
        cache = SimpleCache(exo, NoOpCacheEvictor(), StandaloneDatabaseProvider(context))
    }

    private fun mkdir(file: File): File {
        if (!file.exists()) file.mkdirs()
        return file
    }

    companion object {
        @JvmStatic
        fun get(): CacheManager {
            return Loader.INSTANCE
        }
    }
}

