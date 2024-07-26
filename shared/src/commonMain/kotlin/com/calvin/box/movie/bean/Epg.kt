package com.calvin.box.movie.bean


import com.calvin.box.movie.utils.SimpleDateFormat
import com.calvin.box.movie.utils.myTimeFormat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Epg(
    @SerialName("key") private var key: String = "",
    @SerialName("date") private var date: String =  "",
    @SerialName("epg_data") private var list: List<EpgData> = emptyList()
) {

    companion object {
        fun objectFrom(str: String, key: String, format: SimpleDateFormat): Epg {
            return try {
                val item =  Json.decodeFromString<Epg>(str)
                item.setTime(format)
                item.key = key
                item
            } catch (e: Exception) {
                Epg()
            }
        }

        fun create(key: String, date: String): Epg {
            return Epg().apply {
                this.key = key
                this.date = date
                this.list = ArrayList()
            }
        }
    }


    private fun setTime(format: SimpleDateFormat) {
        list = (ArrayList(LinkedHashSet( list)))
        list.forEach {
            it.startTime = (myTimeFormat(format, date.plus(it.start)))
            it.endTime = (myTimeFormat(format, date.plus(it.end)))
            it.title =(Trans.s2t(it.title))
        }
    }

    fun getEpg(): String {
        return list.firstOrNull { it.selected }?.format() ?: ""
    }

    fun selected(): Epg {
        list.forEach {
            it.selected = (it.isInRange())
        }
        return this
    }

    fun getSelected(): Int {
        return list.indexOfFirst { it.selected }.takeIf { it != -1 } ?: -1
    }

    fun getInRange(): Int {
        return list.indexOfFirst { it.isInRange() }.takeIf { it != -1 } ?: -1
    }
}
