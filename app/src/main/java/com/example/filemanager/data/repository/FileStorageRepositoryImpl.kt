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
import java.io.FileInputStream
import java.security.MessageDigest
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

        return updatedOrNewFiles
    }

    private fun generateChecksum(file: File): String {
        val md = MessageDigest.getInstance("SHA-256") // You can choose a different algorithm if needed, such as MD5 or SHA-1
        val fis = FileInputStream(file)
        val buffer = ByteArray(8192) // Buffer size for reading file chunks
        var bytesRead = fis.read(buffer)

        while (bytesRead != -1) {
            md.update(buffer, 0, bytesRead)
            bytesRead = fis.read(buffer)
        }

        fis.close()
        val digest = md.digest()

        // Convert the byte array to a hexadecimal string
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
            f.toBaseElement()
        } ?: emptyList()
    }
}