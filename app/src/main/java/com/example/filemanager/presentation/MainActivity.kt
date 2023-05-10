package com.example.filemanager.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
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

    private var permissionGranted = MutableStateFlow(false)

    val permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissionGranted.value = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false

        if(permissionGranted.value) {
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
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        permissionGranted.value = hasReadPermission && hasWritePermission || minSdk29


        val permissionsToRequest = mutableListOf<String>()
        if(!hasReadPermission) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!(hasWritePermission || minSdk29)) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateOrRequestPermissions()

        setContent {
            val permissionsGranted by permissionGranted.collectAsState()
            FileManagerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppHost(
                        permissionsGranted = permissionsGranted,
                        shareFile = ::shareFile,
                        openFile = ::openFile
                    )
                }
            }
        }
    }

    private fun shareFile(uri: Uri) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = contentResolver.getType(uri)
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun openFile(uri: Uri) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = contentResolver.getType(uri)
        }
        startActivity(intent)
    }
}