package com.calvin.box.movie.player.extractor

import com.calvin.box.movie.player.Extractor

class Video : Extractor {
    override fun match(scheme: String?, host: String?): Boolean {
        return "video" == scheme
    }

    override fun fetch(url: String?): String {
        if(url == null) return ""
        return url.substring(8)
    }

    override fun stop() {
    }

    override fun exit() {
    }
}
