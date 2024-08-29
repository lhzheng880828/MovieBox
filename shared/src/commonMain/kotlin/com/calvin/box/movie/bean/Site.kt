package com.calvin.box.movie.bean

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.calvin.box.movie.Constant
import com.calvin.box.movie.db.MoiveDatabase
import io.ktor.http.HeadersBuilder
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Entity
@Serializable
data class Site(
    @PrimaryKey(autoGenerate = true)
    @SerialName("id")
    var id: Int = 0,

    @SerialName("key")
    var key: String = "",

    @SerialName("name")
    var name: String = "",

    @SerialName("api")
    var api: String = "",

    @Ignore
    @Serializable(with = ExtAdapter::class)
    @SerialName("ext")
    var ext: String = "",

    @Ignore
    @SerialName("jar")
    var jar: String = "",

    @Ignore
    @SerialName("click")
    var click: String = "",

    @Ignore
    @SerialName("playUrl")
    var playUrl: String = "",

    @Ignore
    @SerialName("type")
    var type: Int = 0,

    @Ignore
    @SerialName("indexs")
    var indexs: Int? = null,

    @Ignore
    @SerialName("timeout")
    var timeout: Int? = null,

    @Ignore
    @SerialName("playerType")
    var playerType: Int? = null,

    @SerialName("searchable")
    var searchable: Int = 1,

    @SerialName("changeable")
    var changeable: Int = 1,

    @Ignore
    @SerialName("categories")
    var categories: List<String> = emptyList(),

    @Ignore
    @SerialName("header")
    var header: JsonElement? = null,

    @Ignore
    @SerialName("style")
    var style: Style? = null,

    @Ignore
    @Transient
    var activated: Boolean = false
) {



    companion object {
        fun objectFrom(element: JsonElement): Site {
            val json = Json { ignoreUnknownKeys= true }
            return try {
                json.decodeFromJsonElement(element)
            } catch (e: Exception) {
                e.printStackTrace()
                Site(key = "0")
            }
        }

        fun get(key: String) = Site(key = key)

        fun get(key: String, name: String) = Site(key = key, name = name)

        fun find(key: String): Site? = runBlocking {  MoiveDatabase.get().getSiteDao().find(key) }
    }

    fun getTimeout() = (timeout ?: Constant.TIMEOUT_PLAY).coerceAtLeast(1) * 1000
    fun getPlayerType() = (playerType ?: -1).coerceAtMost(2)
    fun isIndexs() = getIndexs() == 1
    fun getIndexs(): Int {
        if (/*Setting.isAggregatedSearch()*/true && (indexs == null || indexs == 1)) return 1
        return indexs ?: 0
    }
    fun getStyle(defaultStyle: Style?) = style ?: defaultStyle ?: Style.rect()

    fun isSearchable() = searchable == 1
    fun isChangeable() = changeable == 1
    fun isEmpty() = key.isEmpty() && name.isEmpty()

    fun setSearchable(searchable: Boolean): Site {
        if (this.searchable != 0) this.searchable = if (searchable) 1 else 2
        return this
    }

    fun setChangeable(changeable: Boolean): Site {
        if (this.changeable != 0) this.changeable = if (changeable) 1 else 2
        return this
    }

    fun setActivated(item: Site) {
        activated = this == item
    }



    fun getHeaders(): HeadersBuilder {
        val headers = HeadersBuilder()
        header?.jsonObject?.forEach { (key, value) ->
            headers.append(key, value.jsonPrimitive.content)
        }
        return headers
    }

    fun trans(): Site {
        if (Trans.pass()) return this
        categories = categories.map { Trans.s2t(it) }
        return this
    }

    fun sync(): Site {
        val item = find(key) ?: return this
        if (changeable != 0) changeable = maxOf(1, item.changeable)
        if (searchable != 0) searchable = maxOf(1, item.searchable)
        return this
    }

    suspend fun save() {
        MoiveDatabase.get().getSiteDao().insertOrUpdate(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Site) return false
        return key == other.key
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    override fun toString(): String {
        return "Site(id=$id, key='$key', name='$name', api='$api', ext='$ext', jar='$jar')"
    }


}

object ExtAdapter : JsonTransformingSerializer<String>(String.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return when (element) {
            is JsonPrimitive -> element  // 直接返回字符串
            is JsonObject -> JsonPrimitive(element.toString())  // 将JsonObject转换为字符串
            else -> throw SerializationException("Unknown type for ext")
        }
    }
}