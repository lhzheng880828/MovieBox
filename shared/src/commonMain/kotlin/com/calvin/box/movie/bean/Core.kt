package com.calvin.box.movie.bean


import kotlinx.serialization.Serializable
import com.calvin.box.movie.utils.UrlUtil

@Serializable
data class Core(
    val auth: String =  "",
    val name: String = "",
    val pass: String = "",
    val broker: String = "",
    val resp: String = "",
    val sign: String = "",
    val pkg: String = "",
    val so: String = ""
) {

    fun getMyAuth(): String {
        return if (auth.isEmpty()) "" else UrlUtil.convert(auth)
    }



    fun hook(): Boolean {
        return pkg.isNotEmpty() && sign.isNotEmpty()
    }
}
