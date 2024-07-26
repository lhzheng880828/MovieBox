package com.calvin.box.movie.utils


import kotlinx.serialization.json.*
//import androidx.collection.ArrayMap

object Json {

    fun parse(json: String): JsonElement {
        return try {
            kotlinx.serialization.json.Json.parseToJsonElement(json)
        } catch (e: Throwable) {
            JsonNull
        }
    }

    fun valid(text: String): Boolean {
        return try {
            kotlinx.serialization.json.Json.parseToJsonElement(text)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun invalid(text: String): Boolean = !valid(text)

    fun safeString(obj: JsonObject, key: String): String {
        return try {
            obj[key]?.jsonPrimitive?.content?.trim() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun safeListString(obj: JsonObject, key: String): List<String> {
        val result = mutableListOf<String>()
        if (!obj.containsKey(key)) return result
        when (val element = obj[key]) {
            is JsonObject -> result.add(safeString(obj, key))
            is JsonArray -> element.forEach { result.add(it.jsonPrimitive.content) }
            else -> {}
        }
        return result
    }

    fun safeListElement(obj: JsonObject, key: String): List<JsonElement> {
        val result = mutableListOf<JsonElement>()
        if (!obj.containsKey(key)) return result
        when (val element = obj[key]) {
            is JsonObject -> result.add(element)
            is JsonArray -> element.forEach { result.add(it) }
            else -> {}
        }
        return result
    }

    fun safeObject(element: JsonElement): JsonObject {
        return try {
            when {
                element is JsonObject -> element
                element is JsonPrimitive -> kotlinx.serialization.json.Json.parseToJsonElement(element.content).jsonObject
                else -> JsonObject(emptyMap())
            }
        } catch (e: Exception) {
            JsonObject(emptyMap())
        }
    }

    fun toMap(json: String?): Map<String, String>? {
        return json?.let { toMap(parse(it)) }
    }

    fun toMap(element: JsonElement): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val jsonObject = safeObject(element)
        jsonObject.forEach { (key, value) ->
            map[key] = safeString(jsonObject, key)
        }
        return map
    }

    /*fun toArrayMap(element: JsonElement): ArrayMap<String, String> {
        val map = ArrayMap<String, String>()
        val jsonObject = safeObject(element)
        jsonObject.forEach { (key, value) ->
            map[key] = safeString(jsonObject, key)
        }
        return map
    }*/

    fun toObject(map: Map<String, String>): JsonObject {
        return JsonObject(map.mapValues { JsonPrimitive(it.value) })
    }
}