package com.example.filemanager.presentation.shared.element_details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.filemanager.R

@ExperimentalComposeUiApi
@Composable
fun ElementDetails(
    element: BaseElementDetails,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.details),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        ElementDetailsRow(
            title = stringResource(id = R.string.name),
            content = element.name
        )
        ElementDetailsRow(
            title = stringResource(id = R.string.date),
            content = element.dateModified
        )
        when (element){
            is BaseElementDetails.FileElementDetails -> {
                ElementDetailsRow(
                    title = stringResource(id = R.string.size),
                    content = element.size
                )
            }
            is BaseElementDetails.DirectoryElementDetails -> {
                ElementDetailsRow(
                    title = stringResource(id = R.string.contains),
                    content = pluralStringResource(
                        id = R.plurals.element,
                        count = element.elementsCount,
                        element.elementsCount
                    )
                )
            }
        }
        ElementDetailsRow(
            title = stringResource(id = R.string.path),
            content = element.path
        )
    }
}

@Composable
fun ElementDetailsRow(
    title: String,
    content: String
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = "$title:",
            color = Color.Gray,
            fontSize = 16.sp
        )
        Text(
            text = content,
            fontSize = 16.sp
        )
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewFileElementDetails() {
    ElementDetails(
        BaseElementDetails.FileElementDetails(
            path = "Downloads/test.jpg",
            name = "test.jpg",
            dateModified = "20 Apr 2023 10:20:54",
            size = "2 MB"
        ),
        modifier = Modifier.padding(top = 32.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
    )
}