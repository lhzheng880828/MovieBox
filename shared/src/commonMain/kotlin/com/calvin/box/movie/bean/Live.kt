package com.calvin.box.movie.bean

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.calvin.box.movie.Constant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import com.calvin.box.movie.db.MoiveDatabase
import com.calvin.box.movie.getPlatform
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

@Entity
@Serializable
data class Live(
    @PrimaryKey
    @SerialName("name")
    var name: String = "",

    @Ignore
    @SerialName("type")
    var type: Int = 0,

    @Ignore
    @SerialName("group")
    var group: String = "",

    @Ignore
    @SerialName("url")
    var url: String =  "",

    @Ignore
    @SerialName("jar")
    var jar: String = "",

    @Ignore
    @SerialName("logo")
    var logo: String = "",

    @Ignore
    @SerialName("epg")
    var epg: String = "",

    @Ignore
    @SerialName("ua")
    var ua: String = "",

    @Ignore
    @SerialName("click")
    var click: String = "",

    @Ignore
    @SerialName("origin")
    var origin: String = "",

    @Ignore
    @SerialName("referer")
    var referer: String = "",

    @Ignore
    @SerialName("timeout")
    var timeout: Int? = null,

    @Ignore
    @SerialName("header")
    var header: JsonElement? = null,

    @Ignore
    @SerialName("playerType")
    var playerType: Int? = null,

    @Ignore
    @SerialName("channels")
    var channels: MutableList<Channel> = mutableListOf(),

    @Ignore
    @SerialName("groups")
    var groups: MutableList<Group> = mutableListOf(),

    @Ignore
    @SerialName("catchup")
    var catchup: Catchup = Catchup(),

    @Ignore
    @SerialName("core")
    var core: Core = Core(),

    @SerialName("boot")
    var boot: Boolean = false,

    @SerialName("pass")
    var pass: Boolean = false,

    @Ignore
    @Transient
    var activated: Boolean = false,

    @Ignore
    @Transient
    var width: Int = 0
) {

    companion object {
        fun objectFrom(element: JsonElement): Live {
            return Json.decodeFromJsonElement(Live.serializer(), element)
        }

        fun arrayFrom(str: String): List<Live> {
            return try {
                Json.decodeFromString(str)
            } catch (e: Exception) {
                emptyList()
            }
        }

        fun find(name: String): Live = runBlocking {  MoiveDatabase.get().getLiveDao().find(name) }
    }

    constructor(url: String) : this() {
        this.name =  getPlatform().url2FileName(url)
        this.url = url
    }

    constructor(name: String, url: String) : this() {
        this.name = name
        this.url = url
    }

    fun getTimeout() = (timeout ?: Constant.TIMEOUT_PLAY).coerceAtLeast(1) * 1000
    fun getPlayerType() = (playerType ?: -1).coerceAtMost(2)


    fun check(): Live {
        val proxy = channels.isNotEmpty() &&  channels[0].urls.isNotEmpty() && channels[0].urls[0].startsWith("proxy")
        if (proxy) setProxy()
        return this
    }

    private fun setProxy() {
        url = channels[0]. urls[0]
        name = channels[0].name
        type = 2
    }

    fun find(item: Group): Group {
        for (group in groups) {
            if (group.name == item.name) return group
        }
        groups.add(item)
        return item
    }

    fun getBootIcon() = 0//if (boot) R.drawable.ic_live_boot else R.drawable.ic_live_block
    fun getPassIcon() = 1//if (pass) R.drawable.ic_live_block else R.drawable.ic_live_pass

    fun boot(boot: Boolean): Live {
        this.boot = boot
        return this
    }

    fun pass(pass: Boolean): Live {
        groups.clear()
        this.pass = pass
        return this
    }

    fun sync(): Live {
        val item = find(name) ?: return this
        boot = item.boot
        pass = item.pass
        return this
    }

    fun setActived(item:Live){
        this.activated = item == this
    }

    fun getHeaders(): Map<String, String> {
        if (header == null) return mutableMapOf()
        val headers = com.calvin.box.movie.utils.Json.toMap(header!!).toMutableMap()
        if (ua.isNotEmpty()) headers["User-Agent"] = ua
        if (origin.isNotEmpty()) headers["Origin"] = origin
        if (referer.isNotEmpty()) headers["Referer"] = referer
        return headers
    }

    suspend fun save() {
        MoiveDatabase.get().getLiveDao().insertOrUpdate(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Live) return false
        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}