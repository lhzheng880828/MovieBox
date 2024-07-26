package com.calvin.box.movie.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.calvin.box.movie.bean.Device

@Dao
abstract class DeviceDao : BaseDao<Device>() {
    @Query("SELECT * FROM Device")
    abstract suspend fun findAll(): List<Device>

    @Query("DELETE FROM Device")
    abstract suspend fun delete()
}
