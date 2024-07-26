package com.calvin.box.movie.bean

import androidx.room.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.calvin.box.movie.db.MoiveDatabase
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.pref.BasePreference
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

@Serializable
@Entity(indices = [Index(value = ["url", "type"], unique = true)])
data class Config(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerialName("type")
    var type: Int,

    @SerialName("time")
    var time: Long = Clock.System.now().toEpochMilliseconds(),

    @SerialName("url")
    var url: String = "",

    @SerialName("json")
    var json: String = "",

    @SerialName("name")
    var name: String = "",

    @SerialName("logo")
    var logo: String = "",

    @SerialName("home")
    var home: String = "",

    @SerialName("parse")
    var parse: String = ""
) {

    
        companion object {
            private lateinit var database: MoiveDatabase
            private lateinit var prefApi:BasePreference
            
            fun setDatabase(appDataContainer: AppDataContainer){
                database = appDataContainer.movieRepository.database
                prefApi = appDataContainer.prefApi
            }
            
        fun arrayFrom(str: String): List<Config> {
            return try {
                Json.decodeFromString(str)
            } catch (e: Exception) {
                emptyList()
            }
        }

        fun objectFrom(str: String): Config? {
            return try {
                Json.decodeFromString(str)
            } catch (e: Exception) {
                null
            }
        }
        
            

        fun create(type: Int) = Config(type = type)

        fun create(type: Int, url: String) = Config(type = type, url = url, time = Clock.System.now().toEpochMilliseconds())//runBlocking { .insert()}

        fun create(type: Int, url: String, name: String) = Config(type = type, url = url, name = name, time = Clock.System.now().toEpochMilliseconds())

        fun getAll(type: Int): List<Config> = runBlocking {  database.getConfigDao().findByType(type)}

        fun findUrls(): List<Config> = runBlocking{database.getConfigDao().findUrlByType(0)}

        fun delete(url: String) = runBlocking { database.getConfigDao().delete(url) }

        fun delete(url: String, type: Int) {
           // if (type == 2) Path.clear(FileUtil.getWall(0))
            if (type == 2) runBlocking { database.getConfigDao().delete(type) }
            else runBlocking { database.getConfigDao().delete(url, type) }
        }

          
            
        fun vod() = runBlocking { database.getConfigDao().findOne(0) ?: create(0) }

        fun live() = runBlocking { database.getConfigDao().findOne(1) ?: create(1) }

        fun wall() = runBlocking { database.getConfigDao().findOne(2) ?: create(2) }

        fun find(id: Int) = runBlocking { database.getConfigDao().findById(id) }

        fun find(url: String, type: Int) = runBlocking { database.getConfigDao().find(url, type) ?: create(type, url) }

        fun find(url: String, name: String, type: Int) =
            runBlocking { database.getConfigDao().find(url, type) ?: create(type, url, name) }

        fun find(config: Config) = find(config, config.type)

        fun find(config: Config, type: Int) =
            runBlocking { database.getConfigDao().find(config.url, type) ?: create(type, config.url, config.name) }

        fun find(depot: Depot, type: Int) =
            runBlocking { database.getConfigDao().find(depot.url, type) ?: create(type, depot.url, depot.name) }
    }


    private val configCache:Int
        get() = runBlocking {
        prefApi.configCache.get()
    }

    val isCache: Boolean
        get() = time + (3600 * 1000 * 12 * (configCache)) > Clock.System.now().toEpochMilliseconds()

        fun type(type: Int) = apply { this.type = type }
        fun url(url: String) = apply { this.url = url }
        fun json(json: String) = apply { this.json = json }
        fun name(name: String) = apply { this.name = name }
        fun logo(logo: String) = apply { this.logo = logo }
        fun home(home: String) = apply { this.home = home }
        fun parse(parse: String) = apply { this.parse = parse }

        val isEmpty: Boolean
        get() = url.isEmpty()

        val desc: String
        get() = when {
            name.isNotEmpty() -> name
            url.isNotEmpty() -> url
            else -> ""
        }

       suspend fun insert(): Config {
            if (isEmpty) return this
            database.getConfigDao().insert(this)
            return this
        }

        suspend fun save(): Config {
            if (isEmpty) return this
            database.getConfigDao().update(this)
            return this
        }

        fun update(): Config {
            if (isEmpty) return this
            time = Clock.System.now().toEpochMilliseconds()
            //Prefers.put("config_$type", url)
            return runBlocking {  save()}
        }

        fun delete() {
            runBlocking {  database.getConfigDao().delete(url, type)}
            History.delete(id)
            Keep.delete(id)
        }

    enum class TYPE {
        VOD,
        LIVE,
        WALL
    }
}
