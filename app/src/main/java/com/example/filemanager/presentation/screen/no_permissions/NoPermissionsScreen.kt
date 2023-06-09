package com.example.filemanager.presentation.screen.no_permissions

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.filemanager.R

@Composable
fun NoPermissionsScreen(
    openSettingsForManageAllFilesPermission: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.permissions_needed),
                color = Color.Gray,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 32.dp, end = 32.dp)
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                OutlinedButton(
                    border = null,
                    onClick = openSettingsForManageAllFilesPermission
                ) {
                    Text(stringResource(id = R.string.open_settings))
                }
            }
        }
    }
}