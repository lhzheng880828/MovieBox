package com.calvin.box.movie.xlab.paging

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/7/29
 */

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun SearchField(
    searchTerm: String,
    onEvent: (Event) -> Unit,
    onRefreshList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(searchTerm)) }
    TextField(
        textFieldValue,
        onValueChange = { textFieldValue = it },
        modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
        placeholder = { Text("Search for a repository…") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onEvent(Event.SearchTerm(textFieldValue.text))
                onRefreshList()
            },
        ),
        singleLine = true,
    )
}