package com.calvin.box.movie.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.calvin.box.movie.bean.Track

@Dao
abstract class TrackDao : BaseDao<Track>() {
    @Query("SELECT * FROM Track WHERE `key` = :key")
    abstract suspend fun find(key: String): List<Track>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override suspend fun insert(item: Track): Long

    @Query("DELETE FROM Track WHERE `key` = :key")
    abstract suspend fun delete(key: String)
}
