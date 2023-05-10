package com.example.filemanager.presentation.screen.file_browser

data class StorageVolumeListItem(
    val title: String? = null, // null for phone storage, will get later title by string resource
    val uuid: String? = null
)
