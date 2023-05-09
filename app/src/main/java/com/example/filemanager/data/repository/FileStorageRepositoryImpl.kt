package com.example.filemanager.data.repository

import android.os.Environment
import android.util.Log
import com.example.filemanager.common.Resource
import com.example.filemanager.common.toBaseElement
import com.example.filemanager.data.local.FileDb
import com.example.filemanager.data.local.entity.FileEntity
import com.example.filemanager.domain.model.BaseElement
import com.example.filemanager.domain.repository.FileStorageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class FileStorageRepositoryImpl(
    private val fileDb: FileDb
) : FileStorageRepository {

    private val updatedOrNewFiles: MutableStateFlow<Resource<List<BaseElement.FileElement>>> =
        MutableStateFlow(Resource.Loading())

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val files = gatherUpdatedOrNewFiles(fileDb.fileDao().rowsCount() != 0)
            updatedOrNewFiles.emit(
                Resource.Success(
                    files.map { file -> file.toBaseElement() as BaseElement.FileElement }
                )
            )
        }
    }

    private fun gatherUpdatedOrNewFiles(collectInList: Boolean = true): List<File> {
        val updatedOrNewFiles = mutableListOf<File>()

        Environment.getExternalStorageDirectory().walkTopDown().forEach { file ->
            val fileEntity = fileDb.fileDao().getByPath(file.path)
            if (file.isFile && (fileEntity == null || file.hashCode() != fileEntity.hash)) {
                fileDb.fileDao().upsert(FileEntity(
                    path = file.path,
                    hash = file.hashCode()
                ))

                if (collectInList)
                    updatedOrNewFiles.add(file)
            }
        }

        return updatedOrNewFiles
    }

    override fun getUpdatedOrNewFiles(): StateFlow<Resource<List<BaseElement.FileElement>>> {
        return updatedOrNewFiles
    }

    override fun getElements(path: String): List<BaseElement> {
        val file = File(path)
        return file.listFiles()?.map { f ->
            f.toBaseElement()
        } ?: emptyList()
    }
}