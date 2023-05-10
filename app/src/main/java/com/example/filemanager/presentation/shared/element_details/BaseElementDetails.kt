package com.example.filemanager.presentation.shared.element_details

import android.net.Uri


sealed class BaseElementDetails(
    open val path: String,
    open val name: String,
    open val dateModified: String,
) {

    data class FileElementDetails(
        override val path: String,
        override val name: String,
        override val dateModified: String,
        val size: String,
        val uri: Uri,
    ) : BaseElementDetails(path, name, dateModified)

    data class DirectoryElementDetails(
        override val path: String,
        override val name: String,
        override val dateModified: String,
        val elementsCount: Int,
    ) : BaseElementDetails(path, name, dateModified)
}