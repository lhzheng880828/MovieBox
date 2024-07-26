package com.calvin.box.movie.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Group(
    @SerialName("channel") var channels: MutableList<Channel> = mutableListOf(),
    @SerialName("name") var name: String =  "",
    @SerialName("pass") var pass: String = "",
    var selected: Boolean = false,
    var position: Int = 0,
    var width: Int = 0
) {
    companion object {
        fun arrayFrom(str: String): List<Group> {
            return Json.decodeFromString(str)
        }
        fun create(): Group {
            return create("live settings", false/*R.string.setting_live*/)
        }

        /*fun create(@StringRes resId: Int): Group {
            return Group(ResUtil.getString(resId))
        }*/

        fun create(name: String, pass: Boolean): Group {
             val group = Group()
            group.name = name
            group.position = -1
            if (name.contains("_")) {
                val splits: Array<String> = name.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                group.name = (splits[0])
                if (pass || splits.size == 1) return group
                group.pass = (splits[1])
            }
            if (name.isEmpty())   group.name = "live settings"
            return group
        }

    }


    fun build(name: String, pass: Boolean):Group {
        this.name = name
        this.position = -1
        if (name.contains("_")) parse(pass)
        if (name.isEmpty())  this@Group.name = "live settings"//setName(ResUtil.getString(R.string.setting_live))
        return this
    }


    private fun parse(pass: Boolean) {
        val splits: Array<String> = name.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        name = (splits[0])
        if (pass || splits.size == 1) return
        this.pass = (splits[1])
    }
    fun find(number: Int): Int {
        return channels.lastIndexOf(Channel.create(number))
    }

    fun find(name: String): Int {
        return channels.lastIndexOf(Channel.create(name))
    }

    fun add(channel: Channel) {
        val index: Int = channels.indexOf(channel)
        if (index == -1) channels.add(Channel.create(channel))
        else channels[index].urls.addAll(channel.urls)
    }

    fun find(channel: Channel): Channel {
        val index: Int = channels.indexOf(channel)
        if (index != -1) return channels[index]
        channels.add(channel)
        return channel
    }

    fun current(): Channel {
        return channels[position].group(this)
    }

    fun isKeep():Boolean{
        return name == "Keep"
    }
}
