package com.example.filemanager.data.repository

import android.net.Uri
import android.os.storage.StorageManager
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
import java.io.FileInputStream
import java.security.MessageDigest

class FileStorageRepositoryImpl(
    private val fileUriProvider: (File) -> Uri,
    private val fileDb: FileDb,
    private val storageManager: StorageManager,
) : FileStorageRepository {

    private val updatedOrNewFiles: MutableStateFlow<Resource<List<BaseElement.FileElement>>> =
        MutableStateFlow(Resource.Loading())

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val files = gatherUpdatedOrNewFiles(fileDb.fileDao().rowsCount() != 0)
            updatedOrNewFiles.emit(
                Resource.Success(
                    files.map { file -> file.toBaseElement(fileUriProvider) as BaseElement.FileElement }
                )
            )
        }
    }

    private fun gatherUpdatedOrNewFiles(collectInList: Boolean = true): List<File> {
        val updatedOrNewFiles = mutableListOf<File>()

        storageManager.storageVolumes.forEach { storageVolume ->
            val rootDirectoryPath = if (storageVolume.uuid == null)
                "/storage/emulated/0"
            else
                "/storage/${storageVolume.uuid}"

            File(rootDirectoryPath).walkTopDown().forEach { file ->
                val fileEntity = fileDb.fileDao().getByPath(file.path)
                if (file.isFile) {
                    try {

                        val checksum = generateChecksum(file)
                        Log.d("Generated checksum", file.path + " " + checksum)
                        if (fileEntity == null || checksum != fileEntity.hash) {
                            fileDb.fileDao().upsert(
                                FileEntity(
                                    path = file.path,
                                    hash = checksum
                                )
                            )

                            if (collectInList)
                                updatedOrNewFiles.add(file)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        return updatedOrNewFiles
    }

    private fun generateChecksum(file: File): String {
        val md = MessageDigest.getInstance("SHA-256")
        val fis = FileInputStream(file)
        val buffer = ByteArray(8192)
        var bytesRead = fis.read(buffer)

        while (bytesRead != -1) {
            md.update(buffer, 0, bytesRead)
            bytesRead = fis.read(buffer)
        }

        fis.close()
        val digest = md.digest()

        val hexString = StringBuilder()
        for (i in digest.indices) {
            val hex = Integer.toHexString(0xFF and digest[i].toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }

        return hexString.toString()
    }

    override fun getUpdatedOrNewFiles(): StateFlow<Resource<List<BaseElement.FileElement>>> {
        return updatedOrNewFiles
    }

    override fun getElements(path: String): List<BaseElement> {
        val file = File(path)
        return file.listFiles()?.map { f ->
            f.toBaseElement(fileUriProvider)
        } ?: emptyList()
    }
}