package com.calvin.box.movie.bean


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.calvin.box.movie.api.config.VodConfig
//import com.fongmi.android.tv.event.RefreshEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.calvin.box.movie.db.MoiveDatabase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.runBlocking

@Serializable
@Entity
data class History(
    @PrimaryKey
    var key: String = "",
    var vodPic: String = "",
    var vodName: String = "",
    var vodFlag: String = "",
    var vodRemarks: String = "",
    var episodeUrl: String = "",
    var revSort: Boolean = false,
    var revPlay: Boolean = false,
    var createTime: Long = 0,
    var opening: Long = 0,
    var ending: Long = 0,
    var position: Long = 0,
    var duration: Long = 0,
    var speed: Float = 1f,
    var player: Int = -1,
    var scale: Int = -1,
    var cid: Int = 0
) {

    val siteName: String
        get() = VodConfig.get().getSite(siteKey).name

    val siteKey: String
        get() = key.split(MoiveDatabase.SYMBOL)[0]

    val vodId: String
        get() = key.split(MoiveDatabase.SYMBOL)[1]

    val flag: Flag
        get() = Flag.create(vodFlag)

    val episode: Episode
        get() = Episode.create(vodRemarks, episodeUrl)

    /*val siteVisible: Int
        get() = if (siteName.isNullOrEmpty()) View.GONE else View.VISIBLE

    val revPlayText: Int
        get() = if (revPlay) R.string.play_backward else R.string.play_forward

    val revPlayHint: Int
        get() = if (revPlay) R.string.play_backward_hint else R.string.play_forward_hint*/

    val isNew: Boolean
        get() = createTime == 0L && position == 0L

    private fun checkParam(item: History) {
        if (opening == 0L) opening = item.opening
        if (ending == 0L) ending = item.ending
        if (speed == 1f) speed = item.speed
    }

    private fun merge(items: List<History>, force: Boolean) {
        for (item in items) {
            if (duration > 0 && item.duration > 0 && kotlin.math.abs(duration - item.duration) > 10 * 60 * 1000) continue
            if (!force && key == item.key) continue
            checkParam(item)
            item.delete()
        }
    }

    fun update() {
        merge(find(), false)
        runBlocking {  save() }
    }

    fun update(cid: Int): History {
        return update(cid, find())
    }

    fun update(cid: Int, items: List<History>): History {
        this.cid = cid
        merge(items, true)
        return runBlocking { save() }
    }

   suspend fun save(): History {
       Napier.d { "save history info: $this" }
        MoiveDatabase.get().getHistoryDao().insertOrUpdate(this)
        return this
    }

    fun delete(): History {
      runBlocking {
          MoiveDatabase.get().getHistoryDao().delete(VodConfig.cid, key)
          MoiveDatabase.get().getTrackDao().delete(key)
      }

        return this
    }

    fun find(): List<History> {
       return runBlocking { MoiveDatabase.get().getHistoryDao().findByName(VodConfig.cid, vodName) }
    }

    fun findEpisode(flags: List<Flag>) {
        if (flags.isNotEmpty()) {
            vodFlag = flags[0].flag
            if (flags[0].episodes.isNotEmpty()) {
                vodRemarks = flags[0].episodes[0].name
            }
        }
        for (item in find()) {
            if (position > 0) break
            for (flag in flags) {
                val episode = flag.find(item.vodRemarks, true) ?: continue
                vodFlag = flag.flag
                position = item.position
                vodRemarks = episode.name
                checkParam(item)
                break
            }
        }
    }

    override fun toString(): String = Json.encodeToString(serializer(), this)

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun objectFrom(str: String): History {
            return json.decodeFromString(serializer(), str)
        }

        fun arrayFrom(str: String): List<History> {
            return json.decodeFromString( str)
        }

        fun get(): List<History> = get(VodConfig.cid)

        fun get(cid: Int): List<History> {
            return runBlocking { MoiveDatabase.get().getHistoryDao().find(cid) }
        }

        fun find(key: String): History? {
            return runBlocking {  MoiveDatabase.get().getHistoryDao().find(VodConfig.cid, key)}
        }

        fun delete(cid: Int) {
           runBlocking { MoiveDatabase.get().getHistoryDao().delete(cid) }
        }

        private fun startSync(targets: List<History>) {
            for (target in targets) {
                val items = target.find()
                if (items.isEmpty()) {
                    target.update(VodConfig.cid, items)
                    continue
                }
                for (item in items) {
                    if (target.createTime > item.createTime) {
                        target.update(VodConfig.cid, items)
                        break
                    }
                }
            }
        }

        fun sync(targets: List<History>) {
            // App.execute has been replaced with a more KMP-friendly approach
            // You might want to use a coroutine or a platform-specific threading mechanism here
            startSync(targets)
           //RefreshEvent.history()
        }
    }
}