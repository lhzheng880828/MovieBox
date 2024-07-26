package com.calvin.box.movie.bean


import com.calvin.box.movie.utils.Sniffer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
/*import dev.icerock.moko.parcelize.Parcelable
import com.fongmi.android.tv.App
import com.fongmi.android.tv.utils.Sniffer*/
import kotlinx.serialization.json.Json

@Serializable
data class Vod(
    @SerialName("vod_id")
    var vodId: String = "",

    @SerialName("vod_name")
    var vodName: String =  "",

    @SerialName("type_name")
    var typeName: String = "",

    @SerialName("vod_pic")
    var vodPic: String = "",

    @SerialName("vod_remarks")
    var vodRemarks: String = "",

    @SerialName("vod_year")
    var vodYear: String =  "",

    @SerialName("vod_area")
    var vodArea: String = "",

    @SerialName("vod_director")
    var vodDirector: String = "",

    @SerialName("vod_actor")
    var vodActor: String = "",

    @SerialName("vod_content")
    var vodContent: String = "",

    @SerialName("vod_play_from")
    var vodPlayFrom: String = "",

    @SerialName("vod_play_url")
    var vodPlayUrl: String = "",

    @SerialName("vod_tag")
    var vodTag: String = "",

    @SerialName("cate")
    var cate: Cate? = null,

    @SerialName("style")
    private var style: Style? = null,

    @SerialName("land")
    var land: Int = 0,

    @SerialName("circle")
    var circle: Int = 0,

    @SerialName("ratio")
    var ratio: Float = 0f,

    var vodFlags: MutableList<Flag> = mutableListOf(),

    var site: Site? = null
) {

    companion object {
        fun arrayFrom(str: String): List<Vod> {
            return try {
                Json.decodeFromString<List<Vod>>(str)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }


    fun getFormatVodContent(): String = vodContent.trim().replace("\n", "<br>")


    fun getStyle(): Style? = style ?: Style.get(land, circle, ratio)



    fun getSiteName(): String = site?.name ?: ""
    fun getSiteKey(): String = site?.key ?: ""

    //文件夹
    fun isFolder(): Boolean = "folder" == vodTag || cate != null

    //漫画
    fun isManga(): Boolean = "manga" == vodTag

    fun getStyle(style: Style?): Style = this.style ?: style ?: Style.rect()

    fun getVodPic(pic: String): String {
        if (vodPic.isEmpty()) vodPic = pic
        return vodPic
    }

    fun getVodName(name: String): String {
        if (name.isNotEmpty()) vodName = name
        return vodName
    }

    fun trans() {
        if (Trans.pass()) return
        vodName = Trans.s2t(vodName)
        vodArea = Trans.s2t(vodArea)
        typeName = Trans.s2t(typeName)
        vodRemarks = Trans.s2t(vodRemarks)
        vodActor = if (Sniffer.CLICKER.containsMatchIn(vodActor)) vodActor else Trans.s2t(vodActor)
        vodContent = if (Sniffer.CLICKER.containsMatchIn(vodContent)) vodContent else Trans.s2t(vodContent)
        vodDirector = if (Sniffer.CLICKER.containsMatchIn(vodDirector)) vodDirector else Trans.s2t(vodDirector)
    }

    fun setVodFlags() {
        val playFlags = vodPlayFrom.split("$$$")
        val playUrls = vodPlayUrl.split("$$$")
        for (i in playFlags.indices) {
            if (playFlags[i].isEmpty() || i >= playUrls.size) continue
            val item = Flag.create(playFlags[i].trim())
            item.createEpisode(playUrls[i])
            vodFlags.add(item)
        }
        for (item in vodFlags) {
            if (item.urls == null) continue
            item.createEpisode(item. urls!!)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vod) return false
        return vodId == other.vodId
    }

    override fun hashCode(): Int {
        return vodId.hashCode()
    }

    override fun toString(): String {
        return "Vod(vodName='$vodName', vodId='$vodId', typeName='$typeName')"
    }


}