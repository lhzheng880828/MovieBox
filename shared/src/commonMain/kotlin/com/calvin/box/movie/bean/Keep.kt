package com.calvin.box.movie.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.calvin.box.movie.App
import com.calvin.box.movie.api.config.VodConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.calvin.box.movie.db.MoiveDatabase
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock

@Serializable
@Entity
data class Keep(
    @PrimaryKey
    @SerialName("key")
    var key: String,

    @SerialName("siteName")
    var siteName: String = "",

    @SerialName("vodName")
    var vodName: String = "",

    @SerialName("vodPic")
    var vodPic: String = "",

    @SerialName("createTime")
    var createTime: Long =  Clock.System.now().toEpochMilliseconds(),

    @SerialName("type")
    var type: Int = 0,

    @SerialName("cid")
    var cid: Int = 0
) {
    companion object {
        fun arrayFrom(str: String): List<Keep> {
            return try {
                Json.decodeFromString(str)
            } catch (e: Exception) {
                emptyList()
            }
        }

        fun find(key: String): Keep? = find(VodConfig.cid, key)

        fun find(cid: Int, key: String): Keep? = runBlocking { MoiveDatabase.get().getKeepDao().find(cid, key)}

        fun deleteAll() = runBlocking {  MoiveDatabase.get().getKeepDao().delete() }

        fun delete(cid: Int) = runBlocking {  MoiveDatabase.get().getKeepDao().delete(cid) }

        fun delete(key: String) = runBlocking {  MoiveDatabase.get().getKeepDao().delete(key) }

        fun getVod(): List<Keep> = runBlocking {  MoiveDatabase.get().getKeepDao().getVod() }

        fun getLive(): List<Keep> = runBlocking {  MoiveDatabase.get().getKeepDao().getLive() }

        fun sync(configs: List<Config>, targets: List<Keep>) {
            App.execute {
                startSync(configs, targets)
               // RefreshEvent.keep()
            }
        }

        private fun startSync(configs: List<Config>, targets: List<Keep>) {
            for (target in targets) {
                for (config in configs) {
                    if (target.cid == config.id) {
                       runBlocking { target.save(Config.find(config, 0).id)}
                    }
                }
            }
        }
    }

    val siteKey: String
        get() = key.split(MoiveDatabase.SYMBOL)[0]

    val vodId: String
        get() = key.split(MoiveDatabase.SYMBOL)[1]

    suspend fun save(cid: Int) {
        this.cid = cid
        MoiveDatabase.get().getKeepDao().insertOrUpdate(this)
    }

   suspend fun save() = MoiveDatabase.get().getKeepDao().insert(this)

    suspend fun delete(): Keep {
        MoiveDatabase.get().getKeepDao().delete(cid, key)
        return this
    }
}