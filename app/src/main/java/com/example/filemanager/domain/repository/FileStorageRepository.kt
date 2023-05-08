package com.example.filemanager.domain.repository

import com.example.filemanager.domain.model.BaseElement

interface FileStorageRepository {

    fun getElements(path: String): List<BaseElement>
}