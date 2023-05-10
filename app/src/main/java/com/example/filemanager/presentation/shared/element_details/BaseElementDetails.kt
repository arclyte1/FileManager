package com.example.filemanager.presentation.shared.element_details

import android.net.Uri


sealed class BaseElementDetails(
    open val uri: Uri,
    open val path: String,
    open val name: String,
    open val dateModified: String,
) {

    data class FileElementDetails(
        override val uri: Uri,
        override val path: String,
        override val name: String,
        override val dateModified: String,
        val size: String,
    ) : BaseElementDetails(uri, path, name, dateModified)

    data class DirectoryElementDetails(
        override val uri: Uri,
        override val path: String,
        override val name: String,
        override val dateModified: String,
        val elementsCount: Int,
    ) : BaseElementDetails(uri, path, name, dateModified)
}