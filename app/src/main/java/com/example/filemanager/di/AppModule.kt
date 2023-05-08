package com.example.filemanager.di

import com.example.filemanager.data.repository.FileStorageRepositoryImpl
import com.example.filemanager.domain.repository.FileStorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFileStorageRepository() : FileStorageRepository {
        return FileStorageRepositoryImpl()
    }
}