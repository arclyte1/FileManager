package com.example.filemanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.filemanager.data.local.entity.FileEntity

@Dao
interface FileDao {

    @Query("select * from file where path=:path limit 1")
    fun getByPath(path: String) : FileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(file: FileEntity)

    @Query("select count(*) from file")
    fun rowsCount(): Int
}