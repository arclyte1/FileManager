package com.example.filemanager.domain.model

import java.util.*

sealed class BaseElement(
    open val name: String,
    open val path: String,
    open val dateModified: Date,
) {

    data class FileElement(
        override val name: String,
        override val path: String,
        override val dateModified: Date,
        val extension: String,
        val size: Long,
    ) : BaseElement(name, path, dateModified)

    data class DirectoryElement(
        override val name: String,
        override val path: String,
        override val dateModified: Date,
        val elementsCount: Int,
    ) : BaseElement(name, path, dateModified)
}
