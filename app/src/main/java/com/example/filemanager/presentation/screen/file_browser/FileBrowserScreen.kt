package com.example.filemanager.presentation.screen.file_browser

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.filemanager.R
import com.example.filemanager.presentation.screen.file_browser.components.SelectedVolume
import com.example.filemanager.presentation.shared.element_list_item.ElementListItem
import com.example.filemanager.presentation.screen.file_browser.components.SortingFilter
import com.example.filemanager.presentation.shared.element_details.BaseElementDetails
import com.example.filemanager.presentation.shared.element_details.ElementDetails
import com.example.filemanager.presentation.shared.element_list_item.BaseListElement
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun FileBrowserScreen(
    shareFile: (Uri) -> Unit,
    openFile: (Uri) -> Unit,
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
    val volumesList by viewModel.formattedVolumesList.collectAsState()
    val selectedVolume by viewModel.selectedVolume.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(event) {
        if (event is FileBrowserViewModel.Event.UpdateScrollPosition) {
            val itemPos = (event as FileBrowserViewModel.Event.UpdateScrollPosition).itemPos
            val offset = (event as FileBrowserViewModel.Event.UpdateScrollPosition).offset
            listState.scrollToItem(itemPos, offset)
            viewModel.event.value = FileBrowserViewModel.Event.Clean
        }
    }

    BackHandler {
        if (bottomSheetState.isVisible)
            scope.launch { bottomSheetState.hide() }
        else
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
                    ),
                    sharingFileEnabled = elementDetails is BaseElementDetails.FileElementDetails,
                    shareFile = shareFile
                )
            }
            Divider()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {


                SelectedVolume(
                    volumesList = volumesList,
                    selectedVolume = selectedVolume,
                    selectVolume = viewModel::selectVolume,
                    modifier = Modifier.weight(1f)
                )

                SortingFilter(
                    sortBy = sortBy,
                    sortingOrder = sortingOrder,
                    setSortBy = viewModel::setSortBy,
                    setSortingOrder = viewModel::setSortingOrder,
                )
            }

            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(state = listState) {
                    items(listItems) { element ->
                        ElementListItem(element, modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    when (element) {
                                        is BaseListElement.DirectoryListElement -> {
                                            viewModel.navigateDirectory(
                                                element.name,
                                                listState.firstVisibleItemIndex,
                                                listState.firstVisibleItemScrollOffset
                                            )
                                        }
                                        is BaseListElement.FileListElement -> {
                                            openFile(element.uri)
                                        }
                                    }
                                },
                                onLongClick = {
                                    scope.launch {
                                        viewModel.setElementDetails(element.name)
                                        bottomSheetState.show()
                                    }
                                }
                            )
                            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp))
                    }
                }

                if (listItems.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_files),
                            color = Color.Gray,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 32.dp, end = 32.dp)
                        )
                    }
                }
            }
        }
    }
}