package com.calvin.box.movie.player.extractor

import com.calvin.box.movie.player.Extractor
import com.calvin.box.movie.server.Server.get

class Proxy : Extractor {
    override fun match(scheme: String?, host: String?): Boolean {
        return "proxy" == scheme
    }

    @Throws(Exception::class)
    override fun fetch(url: String?): String {
        return url!!.replace("proxy://", get().getAddress("/proxy?"))
    }

    override fun stop() {
    }

    override fun exit() {
    }
}
