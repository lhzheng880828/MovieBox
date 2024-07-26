package com.calvin.box.movie.player.extractor

import com.calvin.box.movie.player.Extractor

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/7/26
 */
class Thunder :Extractor{
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

     class Parser(url:String){
        companion object {
            private val THUNDER = Regex("(magnet|thunder|ed2k):.*")
            private var url: String? = null
            private var time = 0

            fun match(url: String): Boolean = THUNDER.containsMatchIn(url) || isTorrent(url)

            fun get(url: String): Parser = Parser(url)

            private fun isTorrent(url: String): Boolean =
                !url.startsWith("magnet") && url.split(";")[0].endsWith(".torrent")
        }
    }
}