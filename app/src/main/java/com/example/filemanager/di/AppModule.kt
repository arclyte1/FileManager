package com.example.filemanager.di

import android.content.Context
import androidx.room.Room
import com.example.filemanager.data.local.FileDb
import com.example.filemanager.data.repository.FileStorageRepositoryImpl
import com.example.filemanager.domain.repository.FileStorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFileStorageRepository(
        fileDb: FileDb
    ) : FileStorageRepository {
        return FileStorageRepositoryImpl(fileDb)
    }

    @Provides
    @Singleton
    fun provideFileDb(
        @ApplicationContext context: Context
    ) : FileDb {
        return Room.databaseBuilder(context, FileDb::class.java, "file_db").build()
    }
}