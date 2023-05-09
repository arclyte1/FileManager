package com.example.filemanager.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.filemanager.presentation.screen.file_browser.FileBrowserScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.FileBrowser.screen_route
    ) {
        composable(BottomNavItem.FileBrowser.screen_route) {
            FileBrowserScreen()
        }
        composable(BottomNavItem.Recent.screen_route) {

        }
    }
}