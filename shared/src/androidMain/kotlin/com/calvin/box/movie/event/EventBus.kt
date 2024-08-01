package com.calvin.box.movie.event

import com.calvin.box.movie.bean.*

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/8/1
 */
class ActionEvent(private val action: String) {
    fun getAction(): String {
        return action
    }

    fun isUpdate(): Boolean {
        return UPDATE == getAction()
    }

    companion object {
        var STOP: String = BuildConfig.APPLICATION_ID.concat(".stop")
        var PREV: String = BuildConfig.APPLICATION_ID.concat(".prev")
        var NEXT: String = BuildConfig.APPLICATION_ID.concat(".next")
        var PLAY: String = BuildConfig.APPLICATION_ID.concat(".play")
        var PAUSE: String = BuildConfig.APPLICATION_ID.concat(".pause")
        var UPDATE: String = BuildConfig.APPLICATION_ID.concat(".update")

        fun send(action: String) {
            EventBus.getDefault().post(ActionEvent(action))
        }

        fun update() {
            send(UPDATE)
        }

        fun next() {
            send(NEXT)
        }

        fun prev() {
            send(PREV)
        }

        fun pause() {
            send(PAUSE)
        }
    }
}

object BuildConfig {
    val APPLICATION_ID:String = "com.calvin.box.movie"



}

fun String.concat(suffix: String): String {
    return this + suffix
}



class CastEvent(config: Config, device: Device, history: History) {
    private val config: Config = config
    private val device: Device = device
    private val history: History = history

    fun getHistory(): History {
        return history
    }

    fun getDevice(): Device {
        return device
    }

    fun getConfig(): Config {
        return config
    }

    companion object {
        fun post(config: Config, device: Device, history: History) {
            EventBus.getDefault().post(CastEvent(config, device, history))
        }
    }
}

class ErrorEvent {
    val type: Type
    val retry: Int
    private var msg: String? = null
    var code: Int
        private set

    constructor(type: Type, retry: Int, code: Int) {
        this.type = type
        this.retry = retry
        this.code = code
    }

    constructor(type: Type, retry: Int, code: Int, msg: String?) {
        this.msg = msg
        this.type = type
        this.retry = retry
        this.code = code
    }

    val isUrl: Boolean
        get() = Type.URL == type

    val isDecode: Boolean
        get() = code / 1000 == 4

    fun getMsg(): String? {
        if (type == Type.URL) return "播放地址错误,错误码:$code"//ResUtil.getString(R.string.error_play_url, code)
        if (type == Type.FLAG) return "没有线路数据"//ResUtil.getString(R.string.error_play_flag)
        if (type == Type.PARSE) return "播放地址解析失败"//ResUtil.getString(R.string.error_play_parse)
        if (type == Type.TIMEOUT) return "连接超时"//ResUtil.getString(R.string.error_play_timeout)
        return msg
    }

    enum class Type {
        URL, FLAG, PARSE, TIMEOUT, EXTRACT
    }

    companion object {
        fun url(retry: Int) {
            EventBus.getDefault().post(ErrorEvent(Type.URL, retry, -1))
        }

        fun url(retry: Int, code: Int) {
            EventBus.getDefault().post(ErrorEvent(Type.URL, retry, code))
        }

        fun flag() {
            EventBus.getDefault().post(ErrorEvent(Type.FLAG, 0, -1))
        }

        fun parse() {
            EventBus.getDefault().post(ErrorEvent(Type.PARSE, 0, -1))
        }

        fun timeout() {
            EventBus.getDefault().post(ErrorEvent(Type.TIMEOUT, 0, -1))
        }

        fun extract(msg: String?) {
            EventBus.getDefault().post(ErrorEvent(Type.EXTRACT, 0, -1, msg))
        }
    }
}

class PlayerEvent private constructor(val state: Int) {
    companion object {
        fun prepare() {
            EventBus.getDefault().post(PlayerEvent(0))
        }

        fun state(state: Int) {
            EventBus.getDefault().post(PlayerEvent(state))
        }
    }
}

    class RefreshEvent {
        val type: Type
        var path: String? = null
            private set

        private constructor(type: Type) {
            this.type = type
        }

        constructor(type: Type, path: String?) {
            this.type = type
            this.path = path
        }

        enum class Type {
            CONFIG, IMAGE, VIDEO, HISTORY, KEEP, SIZE, WALL, DETAIL, PLAYER, SUBTITLE, DANMAKU
        }

        companion object {
            fun config() {
                EventBus.getDefault().post(RefreshEvent(Type.CONFIG))
            }

            fun image() {
                EventBus.getDefault().post(RefreshEvent(Type.IMAGE))
            }

            fun video() {
                EventBus.getDefault().post(RefreshEvent(Type.VIDEO))
            }

            fun history() {
                EventBus.getDefault().post(RefreshEvent(Type.HISTORY))
            }

            fun keep() {
                EventBus.getDefault().post(RefreshEvent(Type.KEEP))
            }

            fun size() {
                EventBus.getDefault().post(RefreshEvent(Type.SIZE))
            }

            fun wall() {
                EventBus.getDefault().post(RefreshEvent(Type.WALL))
            }

            fun detail() {
                EventBus.getDefault().post(RefreshEvent(Type.DETAIL))
            }

            fun player() {
                EventBus.getDefault().post(RefreshEvent(Type.PLAYER))
            }

            fun subtitle(path: String?) {
                EventBus.getDefault().post(RefreshEvent(Type.SUBTITLE, path))
            }

            fun danmaku(path: String?) {
                EventBus.getDefault().post(RefreshEvent(Type.DANMAKU, path))
            }
        }
    }

    class ScanEvent(val address: String) {
        companion object {
            fun post(address: String) {
                EventBus.getDefault().post(ScanEvent(address))
            }
        }
    }
class ServerEvent private constructor(val type: Type, val text: String, val name: String = "") {
    enum class Type {
        SEARCH, PUSH, SETTING
    }

    companion object {
        fun search(text: String) {
            EventBus.getDefault().post(ServerEvent(Type.SEARCH, text))
        }

        fun push(text: String) {
            EventBus.getDefault().post(ServerEvent(Type.PUSH, text))
        }

        fun setting(text: String) {
            EventBus.getDefault().post(ServerEvent(Type.SETTING, text))
        }

        fun setting(text: String, name: String) {
            EventBus.getDefault().post(ServerEvent(Type.SETTING, text, name))
        }
    }
}

class StateEvent private constructor(val type: Type) {
    enum class Type {
        EMPTY, PROGRESS, CONTENT
    }

    companion object {
        fun empty() {
            EventBus.getDefault().post(StateEvent(Type.EMPTY))
        }

        fun progress() {
            EventBus.getDefault().post(StateEvent(Type.PROGRESS))
        }

        fun content() {
            EventBus.getDefault().post(StateEvent(Type.CONTENT))
        }
    }
}

class EventBus {

    companion object{
        fun getDefault(): EventBus {
            return EventBus()
        }
    }

    fun post(any: Any){

    }
}