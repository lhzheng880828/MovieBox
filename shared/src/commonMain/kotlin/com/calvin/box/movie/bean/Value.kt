package com.calvin.box.movie.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Value(
    @SerialName("n")
    var n: String = "",
    @SerialName("v")
    var v: String = ""
) {
    private var activated: Boolean = false

  /*  constructor(v: String) : this("", v)

    constructor(n: String, v: String) : this() {

    }*/ /*: this(Trans.s2t(n), v)*/


    fun isActivated(): Boolean {
        return activated
    }

    fun setActivated(activated: Boolean) {
        this.activated = activated
    }

    fun setActivated(item: Value) {
        val equal = item == this
        if (activated && equal) {
            activated = false
        } else {
            activated = equal
        }
    }

    fun trans() {
        n = Trans.s2t(n)
    }
}
