package com.example.filemanager.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val icon: ImageVector, val screen_route: String) {
    object FileBrowser : BottomNavItem(Icons.Default.Folder, "file_browser")
    object Recent : BottomNavItem(Icons.Default.History, "recent")
}