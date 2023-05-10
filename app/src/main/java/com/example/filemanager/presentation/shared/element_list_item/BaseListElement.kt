package com.example.filemanager.presentation.shared.element_list_item

import android.net.Uri


sealed class BaseListElement(
    open val iconId: Int,
    open val name: String,
    open val dateModified: String,
) {

    data class FileListElement(
        override val iconId: Int,
        override val name: String,
        override val dateModified: String,
        val size: String,
        val uri: Uri,
    ) : BaseListElement(iconId, name, dateModified)

    data class DirectoryListElement(
        override val iconId: Int,
        override val name: String,
        override val dateModified: String,
        val elementsCount: Int,
    ) : BaseListElement(iconId, name, dateModified)
}