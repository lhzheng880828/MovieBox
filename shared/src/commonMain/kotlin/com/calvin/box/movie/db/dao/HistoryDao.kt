package com.calvin.box.movie.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.calvin.box.movie.bean.History

@Dao
abstract class HistoryDao : BaseDao<History>() {
    @Query("SELECT * FROM History WHERE cid = :cid ORDER BY createTime DESC")
    abstract suspend fun find(cid: Int): List<History>

    @Query("SELECT * FROM History WHERE cid = :cid AND `key` = :key")
    abstract suspend fun find(cid: Int, key: String): History

    @Query("SELECT * FROM History WHERE cid = :cid AND vodName = :vodName")
    abstract suspend fun findByName(cid: Int, vodName: String): List<History>

    @Query("DELETE FROM History WHERE cid = :cid AND `key` = :key")
    abstract suspend fun delete(cid: Int, key: String)

    @Query("DELETE FROM History WHERE cid = :cid")
    abstract suspend fun delete(cid: Int)

    @Query("DELETE FROM History")
    abstract suspend fun delete()
}
