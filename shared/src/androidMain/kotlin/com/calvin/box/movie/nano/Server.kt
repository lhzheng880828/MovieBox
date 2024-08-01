package com.calvin.box.movie.nano

import com.calvin.box.movie.AndroidPlayers
import com.calvin.box.movie.player.Players
import com.github.catvod.Proxy
import com.github.catvod.utils.Util
import kotlin.concurrent.Volatile

class Server {
    var player: AndroidPlayers? = null
    private var nano: Nano? = null
    private var port: Int = 9978
        private set

    private object Loader {
        @Volatile
        var INSTANCE: Server = Server()
    }

    val address: String
        get() = getAddress(false)

    fun getAddress(tab: Int): String {
        return getAddress(false) + "?tab=" + tab
    }

    fun getAddress(path: String): String {
        return getAddress(true) + "/" + path
    }

    fun getAddress(local: Boolean): String {
        return "http://" + (if (local) "127.0.0.1" else Util.getIp()) + ":" + port
    }

    fun start() {
        if (nano != null) return
        do {
            try {
                nano = Nano(port)
                Proxy.set(port)
                nano!!.start()
                break
            } catch (e: Exception) {
                ++port
                nano!!.stop()
                nano = null
            }
        } while (port < 9999)
    }

    fun stop() {
        if (nano != null) nano!!.stop()
        nano = null
    }

    companion object {
        fun get(): Server {
            return Loader.INSTANCE
        }
    }
}
