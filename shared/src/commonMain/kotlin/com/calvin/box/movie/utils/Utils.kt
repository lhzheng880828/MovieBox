package com.calvin.box.movie.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


object SimpleDateFormat {
    fun format( time:Long, format:String): String {
        val dateTime = Instant.fromEpochMilliseconds(time).toLocalDateTime(TimeZone.currentSystemDefault())
        val year = dateTime.year.toString().padStart(4, '0')
        val month = dateTime.monthNumber.toString().padStart(2, '0')
        val day = dateTime.dayOfMonth.toString().padStart(2, '0')
        val hour = dateTime.hour.toString().padStart(2, '0')
        val minute = dateTime.minute.toString().padStart(2, '0')
        val second = dateTime.second.toString().padStart(2, '0')

        return format
            .replace("yyyy", year)
            .replace("MM", month)
            .replace("dd", day)
            .replace("HH", hour)
            .replace("mm", minute)
            .replace("ss", second)
    }
}

fun myTimeFormat(dateFormat: SimpleDateFormat, timeStr:String ): Long{
    //将字符串时间转成millisecond
    return 0L
}

fun getDigit(text: String): Int {
    return try {
        if (text.startsWith("上") || text.startsWith("下")) return -1
        text.replace(Regex("(?i)(mp4|H264|H265|720p|1080p|2160p|4K)"), "")
            .replace(Regex("\\D+"), "")
            .toInt()
    } catch (e: Exception) {
        -1
    }
}