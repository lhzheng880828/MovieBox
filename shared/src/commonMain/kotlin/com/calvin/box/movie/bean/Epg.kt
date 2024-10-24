package com.calvin.box.movie.bean


import com.calvin.box.movie.utils.SimpleDateFormat
import com.calvin.box.movie.utils.myTimeFormat
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.toInstant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Epg(
    @SerialName("key")  var key: String = "",
    @SerialName("date")  var date: String =  "",
    @SerialName("epg_data") private var list: List<EpgData> = emptyList()
) {

    companion object {
        fun setDateTime(epg: Epg, zone: TimeZone, dateTimeFormat: DateTimeFormat<LocalDateTime>): Epg {
            return try {
                epg.setTime(dateTimeFormat, zone)
                epg
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


    private fun setTime(format: DateTimeFormat<LocalDateTime>, timeZone: TimeZone) {
        list = (ArrayList(LinkedHashSet( list)))
        list.forEach {
            it.startTime =  format.parse(date.plus(it.start)).toInstant(timeZone).toEpochMilliseconds()
            it.endTime =   format.parse(date.plus(it.end)).toInstant(timeZone).toEpochMilliseconds()
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
