package com.example.filemanager.domain.usecase

import com.example.filemanager.domain.model.BaseElement
import com.example.filemanager.domain.repository.FileStorageRepository
import javax.inject.Inject

class GetElementsUseCase @Inject constructor(
    private val repository: FileStorageRepository
) {

    operator fun invoke(path: String): List<BaseElement> {
        return repository.getElements(path)
    }
}