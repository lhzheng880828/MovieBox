package com.calvin.box.movie.api

//import android.net.Uri
//import androidx.core.i18n.DateTimeFormatter
import android.net.Uri
import com.calvin.box.movie.bean.Epg
import com.calvin.box.movie.bean.EpgData
import com.calvin.box.movie.bean.Live
import com.calvin.box.movie.bean.Trans
import com.calvin.box.movie.bean.Tv
import com.calvin.box.movie.utils.Download
import com.calvin.box.movie.utils.FileUtil
import com.github.catvod.utils.Path
import com.github.catvod.utils.Trans
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.*
import org.simpleframework.xml.core.Persister


object AndroidEpgParser {
   // @Throws(java.lang.Exception::class)
    fun start(live: Live): Boolean {
        if (!live.epg.endsWith(".xml") && !live.epg.endsWith(".gz")) return false
        val file: java.io.File = Path.epg(Uri.parse(live.epg).getLastPathSegment())
        if (shouldDownload(file)) Download.create(live.epg, file).start()
        if (file.getName().endsWith(".gz")) readGzip(live, file)
        else readXml(live, file)
        return true
    }

    private fun shouldDownload(file: java.io.File): Boolean {
        return !file.exists() || !isToday(file.lastModified())
    }

    private fun isToday(date: java.util.Date): Boolean {
        return isToday(date.getTime())
    }

    private fun isToday(millis: Long): Boolean {
        val calendar: java.util.Calendar = java.util.Calendar.getInstance()
        calendar.setTimeInMillis(millis)
        return calendar.get(java.util.Calendar.DAY_OF_MONTH) == java.util.Calendar.getInstance()
            .get(java.util.Calendar.DAY_OF_MONTH)
    }

   // @Throws(java.lang.Exception::class)
    private fun readGzip(live: Live, file: java.io.File) {
        val xml: java.io.File = Path.epg(file.getName().replace(".gz", ""))
        if (!xml.exists()) FileUtil.extractGzip(file, xml)
        readXml(live, xml)
    }

  /*  val formatTime: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val formatDate: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formatFull: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss Z")

    fun formatTime(instant: Instant, timeZone: TimeZone): String {
        return formatTime.format(instant.toLocalDateTime(timeZone))
    }

    fun formatDate(instant: Instant, timeZone: TimeZone): String {
        return formatDate.format(instant.toLocalDateTime(timeZone))
    }

    fun formatFull(instant: Instant, timeZone: TimeZone): String {
        return formatFull.format(instant.toLocalDateTime(timeZone))
    }*/


    //@Throws(java.lang.Exception::class)
    private fun readXml(live: Live, file:String) {
        val exist: MutableSet<String> = mutableSetOf()// java.util.HashSet<String>()
        val epgMap: MutableMap<String, Epg> = mutableMapOf()//java.util.HashMap<String, Epg>()
        val mapping: MutableMap<String, String> = mutableMapOf()//java.util.HashMap<String, String>()
        val formatTime: java.text.SimpleDateFormat =
            java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val formatDate: java.text.SimpleDateFormat =
            java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val formatFull: java.text.SimpleDateFormat =
            java.text.SimpleDateFormat("yyyyMMddHHmmss Z", java.util.Locale.getDefault())

        val today: String = formatDate.format(java.util.Date())
        val tv: Tv = Persister().read(Tv::class.java, Path.read(file), false)
        for (group in live.groups) for (channel in group.channels) exist.add(channel.getTvgName())
        for (channel in tv.getChannel()) mapping[channel.getId()] = channel.getDisplayName()
        for (programme in tv.getProgramme()) {
            val key = mapping[programme.getChannel()]
            val startDate: java.util.Date = formatFull.parse(programme.getStart())
            val endDate: java.util.Date = formatFull.parse(programme.getStop())
            if (!exist.contains(key)) continue
            if (!isToday(startDate) && !isToday(endDate)) continue
            if (!epgMap.containsKey(key)) epgMap[key] = Epg.create(key, today)
            val epgData: EpgData = EpgData()
            epgData.title = (Trans.s2t(programme.getTitle()))
            epgData.start = (formatTime.format(startDate))
            epgData.end = (formatTime.format(endDate))
            epgData.startTime = (startDate.getTime())
            epgData.endTime = (endDate.getTime())
            epgMap[key].getList().add(epgData)
        }
        for (group in live.groups) {
            for (channel in group.channels) {
                channel.data = (epgMap[channel.tvgName])
            }
        }
    }
}