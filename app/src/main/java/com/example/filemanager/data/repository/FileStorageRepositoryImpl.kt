package com.example.filemanager.data.repository

import com.example.filemanager.domain.model.BaseElement
import com.example.filemanager.domain.repository.FileStorageRepository
import java.io.File
import java.util.*

class FileStorageRepositoryImpl : FileStorageRepository {

    override fun getElements(path: String): List<BaseElement> {
        val file = File(path)
        return file.listFiles()?.map { f ->
            if (f.isFile) {
                BaseElement.FileElement(
                    name = f.name,
                    path = f.path,
                    dateModified = Date(f.lastModified()),
                    extension = f.extension,
                    size = f.length(),
                )
            } else {
                BaseElement.DirectoryElement(
                    name = f.name,
                    path = f.path,
                    dateModified = Date(f.lastModified()),
                    elementsCount = f.list()?.size ?: 0
                )
            }
        } ?: emptyList()
    }
}