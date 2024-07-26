package com.calvin.box.movie.bean

import com.calvin.box.movie.utils.getDigit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Flag(
    @SerialName("flag")
    var flag: String = "",
    var show: String = "",
    var urls: String? = null,
    @SerialName("episodes")
    var episodes: MutableList<Episode> = mutableListOf(),
    var activated: Boolean = false,
    var position: Int = -1
)  {

    companion object {
        fun create(flag: String): Flag = Flag(flag = flag, show = Trans.s2t(flag))

        fun create(flag: String, name: String, url: String): MutableList<Flag> {
            val item = create(flag)
            item.episodes.add(Episode.create(name, url))
            return mutableListOf(item)
        }
    }

    fun getShowFlag(): String = show ?: flag

    fun createEpisode(data: String) {
        val urls = if (data.contains("#")) data.split("#") else listOf(data)
        urls.forEachIndexed { index, url ->
            val split = url.split("$")
            val number = "${index + 1}".padStart(2, '0')
            val episode = if (split.size > 1) {
                Episode.create(if (split[0].isEmpty()) number else split[0].trim(), split[1])
            } else {
                Episode.create(number, url)
            }
            if (episode !in episodes) episodes.add(episode)
        }
    }

    fun toggle(activated: Boolean, episode: Episode) {
        if (activated) setActivated(episode)
        else episodes.forEach { it.deactivated() }
    }

    private fun setActivated(episode: Episode) {
        position = episodes.indexOf(episode)
        episodes.forEachIndexed { index, item -> item.setSeActivated(index == position) }
    }

    fun find(remarks: String, strict: Boolean): Episode? {
        val number = getDigit(remarks)
        return when {
            episodes.isEmpty() -> null
            episodes.size == 1 -> episodes[0]
            else -> {
                episodes.find { it.rule1(remarks) }
                    ?: episodes.find { it.rule2(number) }
                    ?: if (number == -1) episodes.find { it.rule3(remarks) } else null
                        ?: if (number == -1) episodes.find { it.rule4(remarks) } else null
                            ?: if (position != -1) episodes[position] else null
                                ?: if (!strict) episodes[0] else null
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Flag) return false
        return getShowFlag() == other.getShowFlag()
    }

    override fun hashCode(): Int = getShowFlag().hashCode()

    override fun toString(): String = Json.encodeToString(serializer(), this)
}