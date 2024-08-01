package com.calvin.box.movie.nano

import com.github.catvod.utils.Asset
import com.github.catvod.utils.Path
import com.github.catvod.utils.Shell

object Go {
    private const val GO = "go_proxy_video"

    fun start() {
        Thread {
            val file = Path.cache(GO)
            if (!file.exists()) Path.copy(Asset.open(GO), file)
            Shell.exec("killall -9 " + GO)
            Shell.exec("nohup $file")
        }.start()
    }

    fun stop() {
        Thread { Shell.exec("killall -9 " + GO) }
            .start()
    }
}
