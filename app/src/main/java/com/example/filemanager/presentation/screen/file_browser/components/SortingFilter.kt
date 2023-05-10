package com.example.filemanager.presentation.screen.file_browser.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.filemanager.presentation.screen.file_browser.FileBrowserViewModel

@Composable
fun SortingFilter(
    sortBy: FileBrowserViewModel.SortBy,
    sortingOrder: FileBrowserViewModel.SortingOrder,
    setSortBy: (FileBrowserViewModel.SortBy) -> Unit,
    setSortingOrder: (FileBrowserViewModel.SortingOrder) -> Unit,
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
                text = stringResource(id = sortBy.titleResourceId),
                fontSize = 14.sp,
                color = Color.Black
            )
        }
        DropdownMenu(
            expanded = expandedSortByMenu,
            onDismissRequest = { expandedSortByMenu = false },
            modifier = Modifier
        ) {
            FileBrowserViewModel.SortBy.values().forEach { value ->
                DropdownMenuItem(onClick = {
                    setSortBy(value)
                    expandedSortByMenu = false
                }) {
                    Text(text = stringResource(id = value.titleResourceId))
                }
            }
        }
        val scaleY = if (sortingOrder == FileBrowserViewModel.SortingOrder.ASC) -1f else 1f
        OutlinedButton(
            border = null,
            contentPadding = PaddingValues(0.dp),
            onClick = { setSortingOrder(-sortingOrder) },
            modifier = Modifier
                .height(32.dp)
                .width(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "Sorting order",
                tint = Color.Black,
                modifier = Modifier
                    .scale(1f, scaleY)
            )
        }
    }

}