package com.calvin.box.movie.bean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class Rule(
    @SerialName("name")
     val name: String =  "",

    @SerialName("hosts")
 val hosts: List<String>? = emptyList(),

    @SerialName("regex")
 val regex: List<String>? = emptyList(),

    @SerialName("script")
 val script: List<String> = emptyList(),

    @SerialName("exclude")
 val exclude: List<String> = emptyList(),
) {
    companion object {
        fun arrayFrom( element: JsonElement? ): List<Rule>{
            if(element == null) return emptyList()
           return Json.decodeFromJsonElement(element)
        }

        fun create(name:String):Rule{
            return Rule(name = name)
        }
    }

}