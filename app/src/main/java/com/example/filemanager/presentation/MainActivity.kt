package com.example.filemanager.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.filemanager.BuildConfig
import com.example.filemanager.presentation.ui.theme.FileManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var permissionsGranted = MutableStateFlow(false)

    val permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val hasReadPermission = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        permissionsGranted.value = hasReadPermission
    }

    private fun updateOrRequestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            val hasReadPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            permissionsGranted.value = hasReadPermission

            val permissionsToRequest = mutableListOf<String>()
            if (!hasReadPermission) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (permissionsToRequest.isNotEmpty()) {
                permissionsLauncher.launch(permissionsToRequest.toTypedArray())
            }
        } else {
            // User will press button to grant permissions in settings
            permissionsGranted.value = Environment.isExternalStorageManager()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateOrRequestPermissions()

        if (!permissionsGranted.value)
            lifecycleScope.launchWhenStarted {
                while(!permissionsGranted.value) {
                    val hasReadPermission = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                    permissionsGranted.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                        Environment.isExternalStorageManager() else hasReadPermission
                    delay(500L)
                }
            }

        setContent {
            val permissionsGranted by permissionsGranted.collectAsState()
            FileManagerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppHost(
                        permissionsGranted = permissionsGranted,
                        shareFile = ::shareFile,
                        openFile = ::openFile,
                        openSettingsForManageAllFilesPermission = ::openSettingsForManageAllFilesPermission
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
            setDataAndType(uri, contentResolver.getType(uri))
        }
        startActivity(intent)
    }

    private fun openSettingsForManageAllFilesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    uri
                )
            )
        }
    }
}