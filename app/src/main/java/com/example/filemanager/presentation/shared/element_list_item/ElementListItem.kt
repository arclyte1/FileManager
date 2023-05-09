package com.example.filemanager.presentation.shared.element_list_item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.filemanager.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ElementListItem(
    element: BaseListElement,
    modifier: Modifier = Modifier,
) {
    val style = TextStyle(
        color = Color.DarkGray,
        fontSize = 12.sp,
        letterSpacing = 1.sp
    )
    Row(modifier = modifier.height(48.dp)) {
        Image(
            painter = painterResource(id = element.iconId),
            contentDescription = element.name,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp)
        ) {
            Text(
                text = element.name,
                fontSize = 16.sp,
                letterSpacing = 2.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                when (element) {
                    is BaseListElement.FileListElement -> {
                        Text(
                            text = element.size,
                            style = style
                        )
                        Text(
                            text = element.dateModified,
                            style = style,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                    }
                    is BaseListElement.DirectoryListElement -> {
                        Text(
                            text = pluralStringResource(
                                id = R.plurals.element,
                                count = element.elementsCount,
                                element.elementsCount
                            ),
                            style = style
                        )
                        Text(
                            text = element.dateModified,
                            style = style,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ElementListItemPreview() {
    Column {
        ElementListItem(
            element = BaseListElement.FileListElement(
                R.drawable.base_file,
                "Filename",
                "20 февраля 2023 г.",
                "34 MB"
            ),
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        )
        ElementListItem(
            element = BaseListElement.DirectoryListElement(
                R.drawable.folder,
                "Folder 123",
                "20 февраля 2023 г.",
                15
            ),
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        )
    }
}