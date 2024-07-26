package com.calvin.box.movie.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.calvin.box.movie.bean.Site

@Dao
abstract class SiteDao : BaseDao<Site>() {
    @Query("SELECT * FROM Site WHERE `key` = :key")
    abstract suspend fun find(key: String): Site?
}
