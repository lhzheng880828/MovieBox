package com.calvin.box.movie.bean


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.calvin.box.movie.db.MoiveDatabase
import com.calvin.box.movie.player.Players
import kotlinx.coroutines.runBlocking


@Entity(indices = [Index(value = ["key", "player", "type"], unique = true)])
data class Track(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var type: Int,
    var group: Int = 0,
    var track: Int = 0,
    var player: Int = 0,
    var key: String = "",
    var name: String,
    var selected: Boolean = false,
    var adaptive: Boolean = false
) {
    fun isExo(player: Int): Boolean = this.player == player && player == Players.EXO

    fun isIjk(player: Int): Boolean = this.player == player && player != Players.EXO

    fun toggle(): Track {
        selected = !selected
        return this
    }

    suspend fun save() {
        MoiveDatabase.get().getTrackDao().insert(this)
    }

    companion object {
        fun find(key: String): List<Track?> {
            return runBlocking { MoiveDatabase.get().getTrackDao().find(key)}
        }

        fun delete(key: String) {
           runBlocking { MoiveDatabase.get().getTrackDao().delete(key) }
        }
    }
}