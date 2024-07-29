package com.calvin.box.movie.bean


import com.calvin.box.movie.utils.Sniffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
/*import dev.icerock.moko.parcelize.Parcelable
import com.fongmi.android.tv.App
import com.fongmi.android.tv.utils.Sniffer*/
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames

@Serializable
data class Vod @OptIn(ExperimentalSerializationApi::class) constructor(
    @SerialName("vod_id")
    var vodId: String = "",

    //@Serializable(with = VodNameSerializer::class)
    @JsonNames("vod name", "vod_name")
    var vodName: String =  "",

    @SerialName("type_name")
    var typeName: String = "",

   // @Serializable(with = PicSerializer::class)
    @JsonNames("vod pic", "vod_pic")

    var vodPic: String = "",

    //@Serializable(with = RemarksSerializer::class)
    @JsonNames("vod_remarks", "vod remarks")
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

object VodNameSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("vodName") {
        element<String>("vod_name")
        element<String>("vod name")
    }

    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString(value)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): String {
        return decoder.decodeStructure(descriptor) {
            var result: String? = null
            for (i in 0 until descriptor.elementsCount) {
                val name = descriptor.getElementName(i)
                if (name == "vod_name" || name == "alternative_vod_name") {
                    result = decodeStringElement(descriptor, i)
                    break
                }
            }
            result ?: throw IllegalArgumentException("Missing vod_name or alternative_vod_name field")
        }
    }
}

object PicSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("vodPic") {
        element<String>("vod_pic")
        element<String>("vod pic")
    }

    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString(value)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): String {
        return decoder.decodeStructure(descriptor) {
            var result: String? = null
            for (i in 0 until descriptor.elementsCount) {
                val name = descriptor.getElementName(i)
                if (name == "vod_pic" || name == "vod pic") {
                    result = decodeStringElement(descriptor, i)
                    break
                }
            }
            result ?: throw IllegalArgumentException("Missing vod_pic or alternative_vod_pic field")
        }
    }
}

object RemarksSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("vodRemarks") {
        element<String>("vod_remarks")
        element<String>("vod remarks")
    }

    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString(value)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): String {
        return decoder.decodeStructure(descriptor) {
            var result: String? = null
            for (i in 0 until descriptor.elementsCount) {
                val name = descriptor.getElementName(i)
                if (name == "vod_remarks" || name == "vod remarks") {
                    result = decodeStringElement(descriptor, i)
                    break
                }
            }
            result ?: throw IllegalArgumentException("Missing vod_remarks or alternative_vod_remarks field")
        }
    }
}