package com.calvin.box.movie.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.calvin.box.movie.bean.Live

@Dao
abstract class LiveDao : BaseDao<Live>() {
    @Query("SELECT * FROM Live WHERE name = :name")
    abstract suspend fun find(name: String): Live
}
