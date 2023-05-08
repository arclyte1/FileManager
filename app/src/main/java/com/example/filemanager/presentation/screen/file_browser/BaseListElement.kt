package com.example.filemanager.presentation.screen.file_browser


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
    ) : BaseListElement(iconId, name, dateModified)

    data class DirectoryListElement(
        override val iconId: Int,
        override val name: String,
        override val dateModified: String,
        val elementsCount: Int,
    ) : BaseListElement(iconId, name, dateModified)
}