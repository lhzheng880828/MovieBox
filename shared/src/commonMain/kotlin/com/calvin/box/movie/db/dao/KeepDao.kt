package com.calvin.box.movie.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.calvin.box.movie.bean.Keep

@Dao
abstract class KeepDao : BaseDao<Keep>() {
    @Query("SELECT * FROM Keep WHERE type = 0 ORDER BY createTime DESC")
    abstract suspend fun getVod(): List<Keep>

    @Query("SELECT * FROM Keep WHERE type = 1 ORDER BY createTime DESC")
    abstract suspend fun getLive(): List<Keep>

    @Query("SELECT * FROM Keep WHERE type = 0 AND cid = :cid AND `key` = :key")
    abstract suspend fun find(cid: Int, key: String): Keep?

    @Query("SELECT * FROM Keep WHERE type = 1 AND `key` = :key")
    abstract suspend fun find(key: String): Keep

    @Query("DELETE FROM Keep WHERE type = 1 AND `key` = :key")
    abstract suspend fun delete(key: String)

    @Query("DELETE FROM Keep WHERE type = 0 AND cid = :cid AND `key` = :key")
    abstract suspend fun delete(cid: Int, key: String)

    @Query("DELETE FROM Keep WHERE type = 0 AND cid = :cid")
    abstract suspend fun delete(cid: Int)

    @Query("DELETE FROM Keep WHERE type = 0")
    abstract suspend fun delete()
}
