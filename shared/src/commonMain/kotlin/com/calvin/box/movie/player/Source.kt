package com.calvin.box.movie.player

import com.calvin.box.movie.bean.Episode
import com.calvin.box.movie.bean.Flag
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.player.extractor.Thunder
import com.calvin.box.movie.player.extractor.Youtube
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/7/26
 */
object  Source {

    fun stop(){

    }

    fun fetch(result: Result):String{
        return ""
    }

    private fun processEpisode(episode: Episode): List<Episode> {
        return when {
            Thunder.Parser.match(episode.url) ->  emptyList()//Thunder.Parser.get(episode.url)
            Youtube.Parser.match(episode.url) -> emptyList() //Youtube.Parser.get(episode.url)
            else -> listOf(episode)
        }
    }

    suspend fun parse(coroutineScope: CoroutineScope, flags: List<Flag>) {
        flags.forEach { flag ->
            val newEpisodes = flag.episodes.map { episode ->
                coroutineScope.async { processEpisode(episode) }
            }.awaitAll().flatten()

            flag.episodes.clear()
            flag.episodes.addAll(newEpisodes)
        }
    }
}

interface Extractor {
    fun match(scheme: String?, host: String?): Boolean

    @Throws(Exception::class)
    fun fetch(url: String?): String?

    fun stop()

    fun exit()
}