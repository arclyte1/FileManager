package com.example.filemanager.presentation.screen.file_browser

import android.os.Build
import com.example.filemanager.R
import com.example.filemanager.domain.model.BaseElement
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class ListElementFormatter {

    fun format(element: BaseElement): BaseListElement {
        val dateString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localDate = element.dateModified.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            DateTimeFormatter.ofPattern("d MMMM yyyy").format(localDate)
        } else {
            val dateFormatter = SimpleDateFormat("d MMMM yyyy", Locale.US)
            dateFormatter.format(element.dateModified)
        }

        return when (element) {
            is BaseElement.FileElement -> BaseListElement.FileListElement(
                iconId = getFileIcon(element.extension),
                name = element.name,
                dateModified = dateString,
                size = element.size.toString()
            )
            is BaseElement.DirectoryElement -> BaseListElement.DirectoryListElement(
                iconId = getDirectoryIcon(),
                name = element.name,
                dateModified = dateString,
                elementsCount = element.elementsCount
            )
        }
    }

    private fun getFileIcon(extension: String): Int {
        return when(extension) {
            "tar", "iso", "zip", "rar", "7z", "br", "bz2", "gz", "lz", "lz4", "Z", "tgz", "tlz",
                "xz", "txz", "zst", "war", "xar", "zipx", "zz" -> R.drawable.archive
            "webm", "mkv", "flv", "vob", "avi", "mov", "wmv", "mp4", "m4p", "m4v", "svi", "f4v",
                "f4p", "f4a", "f4b" -> R.drawable.video
            "jpg", "jpeg", "png", "bpm", "gif", "tif", "tiff" -> R.drawable.img
            "mmf", "mp3", "ogg", "wav" -> R.drawable.music
            "doc", "docx", "docm" -> R.drawable.word
            "csv", "xls", "xlsx" -> R.drawable.excel
            "ppt", "pptx" -> R.drawable.powerpoint
            "pdf" -> R.drawable.pdf
            "txt" -> R.drawable.txt
            "apk" -> R.drawable.apk
            else -> R.drawable.base_file
        }
    }

    private fun getDirectoryIcon() = R.drawable.folder
}