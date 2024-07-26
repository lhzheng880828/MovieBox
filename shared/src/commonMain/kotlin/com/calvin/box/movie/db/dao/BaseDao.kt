package com.calvin.box.movie.db.dao

import androidx.room.*

@Dao
abstract class BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(item: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun asyncInsert(item: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(items: List<T>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun asyncInsertList(items: List<T>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(item: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun asyncUpdate(item: T)
    @Update
    abstract suspend fun update(items: List<T>)
    @Update
    abstract suspend fun asyncUpdateList(items: List<T>)

    @Transaction
    open suspend fun insertOrUpdate(item: T) {
        val id = insert(item)
        if (id == -1L) update(item)
    }

    @Transaction
    open suspend fun insertOrUpdate(items: List<T>) {
        val result = insert(items)
        val list: MutableList<T> = mutableListOf()
        for (i in result.indices) if (result[i] == -1L) list.add(items[i])
        if (list.size > 0) update(list)
    }
}
