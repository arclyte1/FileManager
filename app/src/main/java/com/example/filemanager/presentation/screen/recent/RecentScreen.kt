package com.example.filemanager.presentation.screen.recent

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.filemanager.R
import com.example.filemanager.presentation.shared.element_details.ElementDetails
import com.example.filemanager.presentation.shared.element_list_item.ElementListItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun RecentScreen(
    shareFile: (Uri) -> Unit,
    viewModel: RecentViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val fileList by viewModel.formattedFilesList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val elementDetails by viewModel.elementDetails.collectAsState()
    Log.d("Recent", fileList.joinToString(" ") { it.name })

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            if (elementDetails != null) {
                ElementDetails(
                    element = elementDetails!!,
                    shareFile = shareFile,
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
            Text(
                text = stringResource(id = R.string.recent_files),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            )

            LazyColumn {
                items(fileList) { file ->
                    ElementListItem(
                        element = file,
                        modifier = Modifier.combinedClickable(
                            onClick = {
                                //TODO: open file
                            },
                            onLongClick = {
                                scope.launch {
                                    viewModel.setElementDetails(file.name)
                                    bottomSheetState.show()
                                }
                            }
                        ).padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
                    )
                }
            }

            if (fileList.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(id = R.string.last_modified_files_will_appear),
                        color = Color.DarkGray,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(start = 32.dp, end = 32.dp)
                    )
                }
            }
        }
    }
}