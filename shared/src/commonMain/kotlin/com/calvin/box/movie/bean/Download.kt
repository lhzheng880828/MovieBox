package com.calvin.box.movie.bean

//import androidx.annotation.NonNull
import com.calvin.box.movie.HashUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.calvin.box.movie.db.MoiveDatabase
import kotlinx.coroutines.runBlocking

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity
@Serializable
data class Download(
    @PrimaryKey //@NonNull
    val id: String = "0",// HashUtil.md5(url),
    val vodPic: String,
    val vodName: String,
    val url: String,
    val header: String,
    val createTime: Long =  Clock.System.now().toEpochMilliseconds()//System.currentTimeMillis()
) {

    companion object {
        fun objectFrom(str: String): Download {
            return Json.decodeFromString(str)
        }

        fun arrayFrom(str: String): List<Download> {
            return Json.decodeFromString(str)
        }

        fun get(): List<Download> {
            //return AppDatabase.get().getDownloadDao().find()
            return emptyList()
        }

        fun delete(url: String) {
           runBlocking { MoiveDatabase.get().getDownloadDao().delete(HashUtil.md5(url))}
        }

        fun delete(download: Download) {
           runBlocking { MoiveDatabase.get().getDownloadDao().delete(download.id)}
        }

        fun clear() {
            //AppDatabase.get().getDownloadDao().deleteAll()
        }
    }

    fun delete(): Download {
        runBlocking { MoiveDatabase.get().getDownloadDao().delete(id)}
        return this
    }

    suspend fun save(): Download {
          MoiveDatabase.get().getDownloadDao().insertOrUpdate(this)
        return this
    }

   // @NonNull
    override fun toString(): String {
        return Json.encodeToString(this)
    }
}
