package com.calvin.box.movie.player.extractor

import com.calvin.box.movie.player.Extractor

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/7/26
 */
class Youtube :Extractor{
    override fun match(scheme: String?, host: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun fetch(url: String?): String? {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun exit() {
        TODO("Not yet implemented")
    }
     class Parser(url: String){
        companion object {
            private val PATTERN_LIST = Regex("(youtube\\.com|youtu\\.be).*list=")

            fun match(url: String): Boolean = PATTERN_LIST.containsMatchIn(url)

            fun get(url: String): Parser = Parser(url)
        }

    }
}