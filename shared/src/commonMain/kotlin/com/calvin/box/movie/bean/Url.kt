package com.calvin.box.movie.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.min

@Serializable
class Url {
    @SerialName("values")
    private var values: MutableList<Value> =  mutableListOf()

    @SerialName("position")
    private var position: Int = 0

    companion object {
        val json = Json { ignoreUnknownKeys=true }
        fun objectFrom(jsonString: String): Url {
            return try {
                json.decodeFromString(jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
                create()
            }
        }

        fun create(): Url {
            return Url()
        }
    }

    fun toJsonString(): String {
        return Json.encodeToString(this)
    }

    fun getValues(): List<Value> {
        return values
    }

    fun getPosition(): Int {
        return position
    }

    fun v(): String {
        return v(position)
    }

    fun v(position: Int): String {
        return if (position >= getValues().size) "" else getValues()[position].v
    }

    fun n(position: Int): String {
        return if (position >= getValues().size) "" else getValues()[position].n
    }

    fun add(v: String): Url {
        values.add(Value("", v))
        return this
    }

    fun add(n: String, v: String): Url {
        values.add(Value(n, v))
        return this
    }

    fun replace(url: String): Url {
        if (values.isNotEmpty()) {
            values[position].v = url
        }
        return this
    }

    fun set(position: Int): Url {
        this.position = min(position, getValues().size - 1)
        return this
    }

    fun isEmpty(): Boolean {
        return values.isEmpty() ?: true || v().isEmpty()
    }

    fun isMulti(): Boolean {
        return getValues().size > 1
    }
}

