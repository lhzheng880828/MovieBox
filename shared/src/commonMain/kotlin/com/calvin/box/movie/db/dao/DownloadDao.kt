package com.calvin.box.movie.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.calvin.box.movie.bean.Download

@Dao
abstract class DownloadDao : BaseDao<Download>() {
    @Query("SELECT * FROM Download ORDER BY createTime DESC")
    abstract suspend fun find(): List<Download>
    @Query("SELECT * FROM Download WHERE id = :id ORDER BY createTime DESC")
    abstract suspend fun find(id: String?): Download

    @Query("DELETE FROM Download WHERE id = :id")
    abstract suspend fun delete(id: String?)

    @Query("DELETE FROM Download")
    abstract suspend fun delete()
}
