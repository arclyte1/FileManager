package com.example.filemanager.presentation.screen.file_browser

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.filemanager.presentation.shared.element_list_item.ElementListItem
import com.example.filemanager.presentation.screen.file_browser.components.SortingFilter
import com.example.filemanager.presentation.shared.element_details.ElementDetails
import com.example.filemanager.presentation.shared.element_list_item.BaseListElement
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun FileBrowserScreen(
    viewModel: FileBrowserViewModel = hiltViewModel()
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val listItems by viewModel.formattedListElements.collectAsState()
    val event by viewModel.event.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val sortingOrder by viewModel.sortingOrder.collectAsState()
    val elementDetails by viewModel.elementDetails.collectAsState()

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

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            if (elementDetails != null) {
                ElementDetails(
                    element = elementDetails!!,
                    modifier = Modifier.padding(
                        top = 32.dp,
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 16.dp
                    )
                )
            }
            Divider()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
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
                        .combinedClickable(
                            onClick = {
                                if (element is BaseListElement.DirectoryListElement)
                                    viewModel.navigateDirectory(
                                        element.name,
                                        listState.firstVisibleItemIndex,
                                        listState.firstVisibleItemScrollOffset
                                    )
                            },
                            onLongClick = {
                                Log.d("FileBrowserScreen", "Long click")
                                scope.launch {
                                    viewModel.setElementDetails(element.name)
                                    bottomSheetState.show()
                                }
                            }
                        )
                        .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp))
                }
            }
        }
    }
}