package com.calvin.box.movie.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class Doh (
    @SerialName("name")
     var name: String = "",

    @SerialName("url")
     var url: String = "",

    @SerialName("ips")
    val ips: List<String> = emptyList(),
){

    companion object {
        /* use list map
        val hosts: List<Any>?
            get() {
                try {
                    val list: MutableList<java.net.InetAddress> = java.util.ArrayList<java.net.InetAddress>()
                    for (ip in ips!!) list.add(java.net.InetAddress.getByName(ip))
                    return if (list.isEmpty()) null else list
                } catch (ignored: java.lang.Exception) {
                    return null
                }
            }*/

        val json = Json { ignoreUnknownKeys = true }
        fun def(): MutableList<Doh> {
            val items: MutableList<Doh> = mutableListOf()
            val urls: Array<String> = arrayOf("","https://doh.pub/dns-query", "https://dns.alidns.com/dns-query", "https://doh.360.cn/dns-query")//context.getResources().getStringArray(R.array.doh_url)
            val names: Array<String> = arrayOf("System", "Tencent", "Alibaba", "360")//context.getResources().getStringArray(R.array.doh_name)
            for (i in names.indices) items.add(Doh(name =names[i], url = urls[i]))
            return items
        }

        fun encodeToString(doh: Doh):String {
            return json.encodeToString(doh)
        }

        fun objectFrom(str: String): Doh {
            return json.decodeFromString(str)
        }

        fun arrayFrom(element: JsonElement?): List<Doh> {
            if (element == null) return emptyList()
          return  json.decodeFromJsonElement(element)
        }
    }
}
