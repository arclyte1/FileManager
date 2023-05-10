package com.example.filemanager.di

import android.content.Context
import android.net.Uri
import android.os.storage.StorageManager
import androidx.core.content.FileProvider
import androidx.room.Room
import com.example.filemanager.data.local.FileDb
import com.example.filemanager.data.repository.FileStorageRepositoryImpl
import com.example.filemanager.domain.repository.FileStorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFileStorageRepository(
        @ApplicationContext context: Context,
        fileDb: FileDb,
        storageManager: StorageManager
    ) : FileStorageRepository {
        return FileStorageRepositoryImpl(
            fileUriProvider = { file: File ->
                try {
                    FileProvider.getUriForFile(context, context.packageName + ".provider", file)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Uri.EMPTY
                }
            },
            fileDb = fileDb,
            storageManager = storageManager
        )
    }

    @Provides
    @Singleton
    fun provideFileDb(
        @ApplicationContext context: Context
    ) : FileDb {
        return Room.databaseBuilder(context, FileDb::class.java, "file_db").build()
    }

    @Provides
    @Singleton
    fun provideStorageManager(
        @ApplicationContext context: Context
    ): StorageManager {
        return context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
    }
}