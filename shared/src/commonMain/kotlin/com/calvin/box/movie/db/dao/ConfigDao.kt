package com.calvin.box.movie.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.calvin.box.movie.bean.Config
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ConfigDao : BaseDao<Config>() {
    @Query("SELECT * FROM Config WHERE type = :type ORDER BY time DESC")
    abstract suspend fun findByType(type: Int): List<Config>

    @Query("SELECT * FROM Config WHERE type = :type ORDER BY time DESC")
    abstract fun findByTypeFlow(type: Int): Flow<List<Config>>

    //id, name, url, type, time
    @Query("SELECT * FROM Config WHERE type = :type ORDER BY time DESC")
    abstract suspend fun findUrlByType(type: Int): List<Config>

    @Query("SELECT * FROM Config WHERE id = :id")
    abstract suspend fun findById(id: Int): Config

    @Query("SELECT * FROM Config WHERE id = :id")
    abstract fun findByIdFlow(id: Int): Flow<Config>

    @Query("SELECT * FROM Config WHERE type = :type ORDER BY time DESC LIMIT 1")
    abstract suspend fun findOne(type: Int): Config?

    @Query("SELECT * FROM Config WHERE type = :type ORDER BY time DESC LIMIT 1")
    abstract fun findOneFlow(type: Int): Flow<Config?>

    @Query("SELECT * FROM Config WHERE url = :url AND type = :type")
    abstract suspend fun find(url: String, type: Int): Config?

    @Query("SELECT * FROM Config WHERE url = :url AND type = :type")
    abstract fun findFlow(url: String, type: Int): Flow<Config>

    @Query("DELETE FROM Config WHERE url = :url AND type = :type")
    abstract suspend fun delete(url: String, type: Int)

    @Query("DELETE FROM Config WHERE url = :url AND type = :type")
    abstract suspend fun asyncDelete(url: String, type: Int)

    @Query("DELETE FROM Config WHERE url = :url")
    abstract suspend fun delete(url: String)

    @Query("DELETE FROM Config WHERE url = :url")
    abstract suspend fun asyncDelete(url: String)

    @Query("DELETE FROM Config WHERE type = :type")
    abstract suspend fun delete(type: Int)

    @Query("DELETE FROM Config WHERE type = :type")
    abstract suspend fun asyncDelete(type: Int)
}
