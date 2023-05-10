package com.example.filemanager.presentation.screen.file_browser.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.filemanager.R
import com.example.filemanager.presentation.screen.file_browser.StorageVolumeListItem

@Composable
fun SelectedVolume(
    volumesList: List<StorageVolumeListItem>,
    selectedVolume: StorageVolumeListItem,
    selectVolume: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedSortByMenu by remember { mutableStateOf(false) }

    Row(modifier = modifier) {
        OutlinedButton(
            border = null,
            contentPadding = PaddingValues(horizontal = 8.dp),
            onClick = {
                expandedSortByMenu = true
            },
            modifier = Modifier.height(32.dp)
        ) {
            Text(
                text = selectedVolume.title ?: stringResource(id = R.string.phone_storage),
                fontSize = 14.sp,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis
            )
        }
        DropdownMenu(
            expanded = expandedSortByMenu,
            onDismissRequest = { expandedSortByMenu = false },
            modifier = Modifier
        ) {
            volumesList.forEach { volume ->
                DropdownMenuItem(onClick = {
                    selectVolume(volume.uuid)
                    expandedSortByMenu = false
                }) {
                    Text(
                        text = volume.title ?: stringResource(id = R.string.phone_storage),
                        fontSize = 14.sp,
                        color = Color.Black,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}