package com.calvin.box.movie.bean

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

/*
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text
*/

@Serializable
data class Class (
   @Serializable(with = TypeSerializer::class)
    @JsonNames("type id", "type_id")
    var typeId: String = "",

    //@Text
   // @Serializable(with = NameSerializer::class)
    @JsonNames("type name", "type_name")
    var typeName: String = "",

    @SerialName("type_flag")
    var typeFlag: String = "",

    @SerialName("filters")
    var filters: List<Filter> = emptyList(),

    @SerialName("land")
    var land: Int = 0,

    @SerialName("circle")
    var circle: Int = 0,

    @SerialName("ratio")
    var ratio: Float = 0f,

    var filter: Boolean  = false,
    var activated: Boolean = false
)  {

    companion object {
        val json = Json { ignoreUnknownKeys=true }
        fun objectFrom(jsonStr: String): Class {
            return json.decodeFromString(jsonStr)
        }
    }

    fun toggleFilter(): Boolean {
        filter = !filter
        return filter
    }

    fun isHome(): Boolean {
        return "home" == typeId
    }

    fun trans() {
        if (Trans.pass()) return
        typeName = Trans.s2t(typeName.orEmpty())
    }

    fun getStyle(): Style? {
        return Style.get(land, circle, ratio)
    }

    fun getExtend(change: Boolean): HashMap<String, String> {
        val extend = hashMapOf<String, String>()
        filters.forEach { filter ->
            filter.init.let {
                extend[filter.key] = if (change) filter.setActivated(it) else it
            }
        }
        return extend
    }

    override fun hashCode(): Int {
        return typeId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Class

        if (typeId != other.typeId) return false
        if (typeName != other.typeName) return false
        if (typeFlag != other.typeFlag) return false
        if (filters != other.filters) return false
        if (land != other.land) return false
        if (circle != other.circle) return false
        if (ratio != other.ratio) return false
        if (filter != other.filter) return false
        if (activated != other.activated) return false

        return true
    }

    override fun toString(): String {
        return "Class(typeName='$typeName', typeId='$typeId')"
    }


}

object TypeSerializer : JsonTransformingSerializer<String>(String.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return when (element) {
            is JsonPrimitive -> {
                // 如果element不是字符串类型，则将其转换为字符串
                if (!element.isString) {
                    JsonPrimitive(element.content)
                } else {
                    element
                }
            }
            is JsonObject -> JsonPrimitive(element.toString())  // 将JsonObject转换为字符串
            else -> throw SerializationException("Unknown type for type id")
        }
    }
}

object NameSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("typeName") {
        element<String>("type_name")
        element<String>("type name")
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
                if (name == "type_name" || name == "type name") {
                    result = decodeStringElement(descriptor, i)
                    break
                }
            }
            result ?: throw IllegalArgumentException("Missing type_name or alternative_type_name field")
        }
    }
}

