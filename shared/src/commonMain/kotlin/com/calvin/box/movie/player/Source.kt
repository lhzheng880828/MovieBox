package com.calvin.box.movie.player

import com.calvin.box.movie.UrlExtractor
import com.calvin.box.movie.bean.Channel
import com.calvin.box.movie.bean.Flag
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.getUrlExtractor

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

    fun fetch(channel: Channel): String {
       /* val url: String = channel.getCurrent().split("\\$").get(0)
        val extractor: com.fongmi.android.tv.player.Source.Extractor = getExtractor(url)
        if (extractor != null) channel.setParse(0)
        if (extractor is Video) channel.setParse(1)
        return if (extractor == null) url else extractor.fetch(url)*/
       return getPlatformUrlExtractor().fetch(channel)
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