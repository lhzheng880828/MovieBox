package com.calvin.box.movie.player

import com.calvin.box.movie.UrlExtractor
import com.calvin.box.movie.bean.Episode
import com.calvin.box.movie.bean.Flag
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.getUrlExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/7/26
 */
object  Source {

    private var urlExtractor:UrlExtractor? =  null

    private fun getPlatformUrlExtractor():UrlExtractor{
        if(urlExtractor == null){
            urlExtractor = getUrlExtractor()
        }
        return urlExtractor!!
    }

    fun stop(){
        getPlatformUrlExtractor().stop()
    }

    fun exit(){
        getPlatformUrlExtractor().exit()
    }

    fun fetch(result: Result):String{
        return getPlatformUrlExtractor().fetch(result)
    }



    fun parse(flags: List<Flag>) {
        getPlatformUrlExtractor().parse(flags)
    }
}

interface Extractor {
    fun match(scheme: String?, host: String?): Boolean

    @Throws(Exception::class)
    fun fetch(url: String?): String?

    fun stop()

    fun exit()
}