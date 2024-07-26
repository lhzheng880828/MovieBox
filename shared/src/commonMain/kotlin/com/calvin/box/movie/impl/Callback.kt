package com.calvin.box.movie.impl

interface Callback {
    fun success()
    fun error(msg:String)
}