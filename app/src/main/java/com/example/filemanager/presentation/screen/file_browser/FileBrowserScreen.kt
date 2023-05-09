package com.example.filemanager.presentation.screen.file_browser

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.filemanager.presentation.screen.file_browser.components.ElementListItem
import com.example.filemanager.presentation.screen.file_browser.components.SortingFilter

@Composable
fun FileBrowserScreen(
    viewModel: FileBrowserViewModel = hiltViewModel()
) {
    val listState = rememberLazyListState()
    val listItems by viewModel.formattedListElements.collectAsState()
    val event by viewModel.event.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val sortingOrder by viewModel.sortingOrder.collectAsState()

    LaunchedEffect(event) {
        if (event is FileBrowserViewModel.Event.UpdateScrollPosition) {
            val itemPos = (event as FileBrowserViewModel.Event.UpdateScrollPosition).itemPos
            val offset = (event as FileBrowserViewModel.Event.UpdateScrollPosition).offset
            listState.scrollToItem(itemPos, offset)
            viewModel.event.value = FileBrowserViewModel.Event.Clean
        }
    }
    BackHandler {
        viewModel.navigateUp()
    }

    Column {
        Row(modifier = Modifier
            .align(Alignment.End)
            .padding(8.dp)) {
            SortingFilter(
                sortBy = sortBy,
                sortingOrder = sortingOrder,
                setSortBy = viewModel::setSortBy,
                setSortingOrder = viewModel::setSortingOrder
            )
        }
        
        LazyColumn(state = listState) {
            items(listItems) { element ->
                ElementListItem(element, modifier = Modifier
                    .clickable {
                        if (element is BaseListElement.DirectoryListElement)
                            viewModel.navigateDirectory(
                                element.name,
                                listState.firstVisibleItemIndex,
                                listState.firstVisibleItemScrollOffset
                            )
                    }
                    .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp))
            }
        }
    }
}