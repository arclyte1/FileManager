package com.example.filemanager.common

import android.net.Uri
import com.example.filemanager.domain.model.BaseElement
import java.io.File
import java.util.*

fun File.toBaseElement(
    fileUriProvider: (File) -> Uri
) : BaseElement {
    return if (isFile) {
        BaseElement.FileElement(
            uri = fileUriProvider(this),
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