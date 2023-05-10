package com.example.filemanager.presentation

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.filemanager.presentation.ui.theme.FileManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val storageManager = getSystemService(Context.STORAGE_SERVICE) as StorageManager


        Log.d("MainActivity", Environment.getRootDirectory().path + "   " + storageManager.storageVolumes.joinToString(" ") { it.directory?.path ?: "-1" })

        setContent {
            FileManagerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppHost()
                }
            }
        }
    }
}