package com.example.filemanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.filemanager.data.local.dao.FileDao
import com.example.filemanager.data.local.entity.FileEntity

@Database(
    entities = [
        FileEntity::class
    ],
    version = 1
)
abstract class FileDb : RoomDatabase() {

    abstract fun fileDao(): FileDao
}