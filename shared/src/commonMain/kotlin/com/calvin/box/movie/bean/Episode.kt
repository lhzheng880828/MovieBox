package com.calvin.box.movie.bean

import com.calvin.box.movie.utils.getDigit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
data class Episode(
    @SerialName("name")
    var name: String,
    @SerialName("desc")
    var desc: String = "",
    @SerialName("url")
    var url: String = "",
    var index: Int = 0,
    var number: Int = getDigit(name),
    var activated: Boolean = false,
    var selected: Boolean = false,
) {
    companion object {
        fun objectFrom(str: String): Episode = Json.decodeFromString(str)

        fun create(name: String, url: String): Episode {
            return Episode(name, "", url)
        }

        fun create(name: String, desc: String, url: String): Episode {
            return Episode(name, desc, url)
        }


        private fun Episode(name: String, desc: String, url: String):Episode {
            val episode = Episode(name)
            episode.number = getDigit(name)
            episode.name = Trans.s2t(name)
            episode.desc = Trans.s2t(desc)
            episode.url = url
            return episode
        }
    }
    init {
        name = Trans.s2t(name)
        desc = Trans.s2t(desc)
    }

    fun setSeActivated(activated: Boolean) {
        this.activated = activated
        this.selected = activated
    }

    fun deactivated() {
        setSeActivated(false)
    }

    fun rule1(name: String): Boolean = this.name.equals(name, ignoreCase = true)

    fun rule2(number: Int): Boolean = this.number == number && number != -1

    fun rule3(name: String): Boolean = this.name.lowercase().contains(name.lowercase())

    fun rule4(name: String): Boolean = name.lowercase().contains(this.name.lowercase())

   // fun equals(episode: Episode): Boolean = rule1(episode.name)
}