package com.example.filemanager.presentation

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.filemanager.presentation.navigation.BottomNav
import com.example.filemanager.presentation.navigation.BottomNavGraph

@Composable
fun AppHost(
    shareFile: (Uri) -> Unit
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNav(navController = navController) }
    ) { paddingValues ->
        BottomNavGraph(
            navController = navController,
            shareFile = shareFile,
            modifier = Modifier.padding(paddingValues)
        )
    }
}