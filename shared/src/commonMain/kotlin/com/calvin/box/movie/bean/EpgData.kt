package com.calvin.box.movie.bean

import io.ktor.util.date.*
import kotlinx.datetime.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpgData(
    @SerialName("title")  var title: String =  "",
    @SerialName("start")  var start: String = "",
    @SerialName("end")  var end: String = "",
    var selected: Boolean = false,
     var startTime: Long = 0,
     var endTime: Long = 0,
) {

    fun isInRange(): Boolean {
        val currentTime = getTimeMillis()
        return currentTime in startTime..endTime
    }

    fun isFuture(): Boolean {
        return startTime > getTimeMillis()
    }

    /*fun format(group: String): String {
        val pattern = group.split("\\)")[1].split("\\}")[0]
        return when {
            group.contains("(b)") -> SimpleDateFormat(pattern, Locale.getDefault()).format(startTime)

            group.contains("(e)") -> SimpleDateFormat(pattern, Locale.getDefault()).format(endTime)
            else -> ""
        }
    }*/
    fun format(group: String): String {
        val pattern = group.split("\\)")[1].split("\\}")[0]

        val startDateTime = Instant.fromEpochMilliseconds(startTime).toLocalDateTime(TimeZone.currentSystemDefault())
        val endDateTime = Instant.fromEpochMilliseconds(endTime).toLocalDateTime(TimeZone.currentSystemDefault())

        return when {
            group.contains("(b)") -> formatDateTime(startDateTime, pattern)
            group.contains("(e)") -> formatDateTime(endDateTime, pattern)
            else -> ""
        }
    }

    fun formatDateTime(dateTime: LocalDateTime, pattern: String): String {
        // This function will format the LocalDateTime based on the provided pattern
        // Implement custom formatting logic based on the pattern here
        // Since kotlinx.datetime does not have a built-in formatter, you will need to handle it manually
        // Example: Implement basic formatting for common patterns

        val year = dateTime.year.toString().padStart(4, '0')
        val month = dateTime.monthNumber.toString().padStart(2, '0')
        val day = dateTime.dayOfMonth.toString().padStart(2, '0')
        val hour = dateTime.hour.toString().padStart(2, '0')
        val minute = dateTime.minute.toString().padStart(2, '0')
        val second = dateTime.second.toString().padStart(2, '0')

        return pattern
            .replace("yyyy", year)
            .replace("MM", month)
            .replace("dd", day)
            .replace("HH", hour)
            .replace("mm", minute)
            .replace("ss", second)
    }

    fun format(): String {
        return when {
            title.isEmpty() -> ""
            start.isEmpty() && end.isEmpty() -> "Now play $title" //ResUtil.getString(R.string.play_now, getTitle())
            else -> "$start ~ $end  $title"
        }
    }

    fun getTime(): String {
        return when {
            start.isEmpty() && end.isEmpty() -> ""
            else -> "$start ~ $end"
        }
    }



    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + selected.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EpgData

        if (title != other.title) return false
        if (start != other.start) return false
        if (end != other.end) return false
        if (selected != other.selected) return false
        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false

        return true
    }


}
