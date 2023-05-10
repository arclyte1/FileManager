package com.example.filemanager.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.filemanager.presentation.screen.file_browser.FileBrowserScreen
import com.example.filemanager.presentation.screen.recent.RecentScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    shareFile: (Uri) -> Unit,
    openFile: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.FileBrowser.screen_route,
        modifier = modifier
    ) {
        composable(BottomNavItem.FileBrowser.screen_route) {
            FileBrowserScreen(
                shareFile = shareFile,
                openFile = openFile
            )
        }
        composable(BottomNavItem.Recent.screen_route) {
            RecentScreen(
                shareFile = shareFile,
                openFile = openFile
            )
        }
    }
}