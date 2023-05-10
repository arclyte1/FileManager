package com.example.filemanager.domain.model

import android.net.Uri
import java.util.*

sealed class BaseElement(
    open val uri: Uri,
    open val name: String,
    open val path: String,
    open val dateModified: Date,
) {

    data class FileElement(
        override val uri: Uri,
        override val name: String,
        override val path: String,
        override val dateModified: Date,
        val extension: String,
        val size: Long,
    ) : BaseElement(uri, name, path, dateModified)

    data class DirectoryElement(
        override val uri: Uri,
        override val name: String,
        override val path: String,
        override val dateModified: Date,
        val elementsCount: Int,
    ) : BaseElement(uri, name, path, dateModified)
}
