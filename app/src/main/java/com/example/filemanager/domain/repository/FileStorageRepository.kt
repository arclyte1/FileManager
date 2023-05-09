package com.example.filemanager.domain.repository

import com.example.filemanager.common.Resource
import com.example.filemanager.domain.model.BaseElement
import kotlinx.coroutines.flow.StateFlow

interface FileStorageRepository {

    fun getUpdatedOrNewFiles(): StateFlow<Resource<List<BaseElement.FileElement>>>

    fun getElements(path: String): List<BaseElement>
}