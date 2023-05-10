package com.example.filemanager.presentation.shared.element_details

import android.os.Build
import com.example.filemanager.domain.model.BaseElement
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class ElementDetailsFormatter {

    fun format(
        element: BaseElement,
        basePathToRemove: String = "",
        basePathToAdd: String = ""
    ): BaseElementDetails {
        val datePattern = "d MMMM yyyy  H:mm:ss"
        val dateString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localDateTime = element.dateModified.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            DateTimeFormatter.ofPattern(datePattern).format(localDateTime)
        } else {
            val dateFormatter = SimpleDateFormat(datePattern, Locale.US)
            dateFormatter.format(element.dateModified)
        }

        return when (element) {
            is BaseElement.FileElement -> BaseElementDetails.FileElementDetails(
                uri = element.uri,
                path = basePathToAdd + element.path.removePrefix(basePathToRemove),
                name = element.name,
                dateModified = dateString,
                size = bytesToHumanReadableSize(element.size.toDouble())
            )
            is BaseElement.DirectoryElement -> BaseElementDetails.DirectoryElementDetails(
                path = basePathToAdd + element.path.removePrefix(basePathToRemove),
                name = element.name,
                dateModified = dateString,
                elementsCount = element.elementsCount
            )
        }
    }

    private fun bytesToHumanReadableSize(bytes: Double) = when {
        bytes >= 1 shl 30 -> "%.1f GB".format(bytes / (1 shl 30))
        bytes >= 1 shl 20 -> "%.1f MB".format(bytes / (1 shl 20))
        bytes >= 1 shl 10 -> "%.0f kB".format(bytes / (1 shl 10))
        else -> "$bytes bytes"
    }
}