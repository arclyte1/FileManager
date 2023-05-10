package com.example.filemanager.common

import androidx.core.net.toUri
import com.example.filemanager.domain.model.BaseElement
import java.io.File
import java.util.*

fun File.toBaseElement() : BaseElement {
    return if (isFile) {
        BaseElement.FileElement(
            uri = toUri(),
            name = name,
            path = path,
            dateModified = Date(lastModified()),
            extension = extension,
            size = length(),
        )
    } else {
        BaseElement.DirectoryElement(
            uri = toUri(),
            name = name,
            path = path,
            dateModified = Date(lastModified()),
            elementsCount = list()?.size ?: 0
        )
    }
}