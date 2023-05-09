package com.example.filemanager.domain.usecase

import com.example.filemanager.domain.repository.FileStorageRepository
import javax.inject.Inject

class GetUpdatedOrNewFilesUseCase @Inject constructor(
    private val repository: FileStorageRepository
) {

    operator fun invoke() = repository.getUpdatedOrNewFiles()
}