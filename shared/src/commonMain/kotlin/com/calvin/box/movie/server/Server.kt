package com.calvin.box.movie.server

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*

object Server {
    private var nano: Nano? = null
    private val port = atomic(9978)

    fun get(): Server = this

    fun getPort(): Int = port.value

    fun getAddress(): String = getAddress(false)

    fun getAddress(tab: Int): String = "${getAddress(false)}?tab=$tab"

    fun getAddress(path: String): String = "${getAddress(true)}/$path"

    fun getAddress(local: Boolean): String {
        val ip = if (local) "127.0.0.1" else Util.getIp()
        return "http://$ip:${getPort()}"
    }

    fun start() {
        if (nano != null) return
        runBlocking {
            while (port.value < 9999) {
                try {
                    nano = Nano(port.value)
                    Proxy.set(port.value)
                    nano?.start()
                    break
                } catch (e: Exception) {
                    port.incrementAndGet()
                    nano?.stop()
                    nano = null
                }
            }
        }
    }

    fun stop() {
        nano?.stop()
        nano = null
    }
}

// Dummy implementation for KMP compatibility, replace with actual implementation
object Util {
    fun getIp(): String {
        // Implement this method to return the appropriate IP address
        return "192.168.1.1"
    }
}

// Dummy implementation for KMP compatibility, replace with actual implementation
class Nano(private val port: Int) {
    fun start() {
        // Implement the logic to start the server
    }

    fun stop() {
        // Implement the logic to stop the server
    }
}

// Dummy implementation for KMP compatibility, replace with actual implementation
object Proxy {
    fun set(port: Int) {
        // Implement this method to set the proxy port
    }
}
