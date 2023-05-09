package com.example.filemanager.common

import com.example.filemanager.domain.model.BaseElement
import java.io.File
import java.util.*

fun File.toBaseElement() : BaseElement {
    return if (isFile) {
        BaseElement.FileElement(
            name = name,
            path = path,
            dateModified = Date(lastModified()),
            extension = extension,
            size = length(),
        )
    } else {
        BaseElement.DirectoryElement(
            name = name,
            path = path,
            dateModified = Date(lastModified()),
            elementsCount = list()?.size ?: 0
        )
    }
}