package com.calvin.box.movie

object App {

    fun execute(runnable: () -> Unit){
        runnable.invoke()
    }

    fun post(runnable: () -> Unit){
        runnable.invoke()
    }

    fun get():Any {
        return ContextProvider.context
    }

}

interface Runnable{
    fun run()
}

fun run(){

}