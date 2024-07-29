package com.calvin.box.movie.bean


/*import com.google.common.base.Predicates
import com.google.common.collect.Iterables*/
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class Filter (
    @SerialName("key")
    var key: String = "",
    @SerialName("name")
    private var name: String = "",
    @SerialName("init")
    var init: String = "",
    @SerialName("value")
    private var values: List<Value> = emptyList()

){

    fun setActivated(v: String): String {
        val index = values.indexOf(Value(v))
        if (index != -1) values[index].setActivated(true)
        return v
    }

    /*fun check(): Filter {
        Iterables.removeIf(values, Predicates.isNull())
        return this
    }*/

    fun trans(): Filter {
        if (Trans.pass()) return this
        for (v in values) v.trans()
        return this
    }

    override fun toString(): String {
        return "Filter(key='$key', name='$name', init='$init', values=$values)"
    }

    companion object {
        fun objectFrom(element: JsonElement): Filter {
            return Json.decodeFromJsonElement(element)
        }

        fun arrayFrom(result: String): List<Filter> {
           return Json.decodeFromString(result)
        }
    }


}
