package com.calvin.box.movie.player

import com.calvin.box.movie.Constant
import com.calvin.box.movie.bean.Channel
import com.calvin.box.movie.bean.Episode
import com.calvin.box.movie.bean.Flag
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.player.extractor.Force
import com.calvin.box.movie.player.extractor.JianPian
import com.calvin.box.movie.player.extractor.Proxy
import com.calvin.box.movie.player.extractor.Push
import com.calvin.box.movie.player.extractor.TVBus
import com.calvin.box.movie.player.extractor.Thunder
import com.calvin.box.movie.player.extractor.Video
import com.calvin.box.movie.player.extractor.Youtube
import com.calvin.box.movie.utils.UrlUtil.host
import com.calvin.box.movie.utils.UrlUtil.scheme
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.Volatile

/*
*Author:cl
*Email:lhzheng@grandstream.cn
*Date:2024/9/2
*/
class AndroidSource private constructor() {
    private val extractors: MutableList<Extractor>?

    private object Loader {
        @Volatile
        var INSTANCE: AndroidSource = AndroidSource()
    }

    init {
        extractors = ArrayList()
        extractors.add(Force())
        extractors.add(JianPian())
        extractors.add(Proxy())
        extractors.add(Push())
        extractors.add(Thunder())
        extractors.add(TVBus())
        extractors.add(Video())
        extractors.add(Youtube())
    }

    private fun getExtractor(url: String): Extractor? {
        val host = host(url)
        val scheme = scheme(url)
        for (extractor in extractors!!) if (extractor.match(scheme, host)) return extractor
        return null
    }

    private fun addCallable(
        iterator: MutableIterator<Episode>,
        items: MutableList<Callable<List<Episode>>>
    ) {
        val url = iterator.next().url
        if (Thunder.Parser.match(url)) {
            items.add(Thunder.Parser.get(url))
            iterator.remove()
        } else if (Youtube.Parser.match(url)) {
            items.add(Youtube.Parser.get(url))
            iterator.remove()
        }
    }

    @Throws(Exception::class)
    fun parse(flags: List<Flag>) {
        for ((_, _, _, episodes) in flags) {
            val executor = Executors.newFixedThreadPool(Constant.THREAD_POOL)
            val items: MutableList<Callable<List<Episode>>> = ArrayList()
            val iterator = episodes.iterator()
            while (iterator.hasNext()) addCallable(iterator, items)
            for (future in executor.invokeAll(
                items,
                30,
                TimeUnit.SECONDS
            )) episodes.addAll(future.get())
            executor.shutdownNow()
        }
    }

    @Throws(Exception::class)
    fun fetch(result: Result): String {
        val url = result.url.v()
        val extractor = getExtractor(url)
        if (extractor != null) result.parse = 0
        if (extractor is Video) result.parse = 1
        return if (extractor == null) url else extractor.fetch(url)!!
    }

    @Throws(Exception::class)
    fun fetch(channel: Channel): String {
        val url = channel.getCurrent().split("\\$".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0]
        val extractor = getExtractor(url)
        if (extractor != null) channel.parse = 0
        if (extractor is Video) channel.parse = 1
        return if (extractor == null) url else extractor.fetch(url)!!
    }

    fun stop() {
        if (extractors == null) return
        for (extractor in extractors) extractor.stop()
    }

    fun exit() {
        if (extractors == null) return
        for (extractor in extractors) extractor.exit()
    }

    companion object {
        fun get(): AndroidSource {
            return Loader.INSTANCE
        }
    }
}
