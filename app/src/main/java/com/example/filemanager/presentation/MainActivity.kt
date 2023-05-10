package com.example.filemanager.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.filemanager.presentation.ui.theme.FileManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var readPermissionGranted = MutableStateFlow(false)

    val permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        readPermissionGranted.value = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted.value

        if(readPermissionGranted.value) {
            // TODO
        } else {
            Toast.makeText(this, "Can't read files without permission.", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateOrRequestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        readPermissionGranted.value = hasReadPermission

        val permissionsToRequest = mutableListOf<String>()
        if(!readPermissionGranted.value) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if(permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateOrRequestPermissions()

        setContent {
            val permissionsGranted by readPermissionGranted.collectAsState()
            FileManagerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppHost(
                        permissionsGranted = permissionsGranted,
                        shareFile = ::shareFile
                    )
                }
            }
        }
    }

    private fun shareFile(uri: Uri) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "*/*"
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }
}