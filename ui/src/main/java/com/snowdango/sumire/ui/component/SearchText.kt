package com.snowdango.sumire.ui.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.snowdango.sumire.ui.theme.SumireTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchText(
    modifier: Modifier,
    searchSuggestList: List<String>,
    onSearchTextChange: (searchText: String) -> Unit,
    onSearch: (searchText: String) -> Unit,
) {
    var searchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    DockedSearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = searchText,
                onQueryChange = { newValue ->
                    searchText = newValue
                    onSearchTextChange.invoke(newValue)
                    Log.d("searchTextNewValue", newValue)
                },
                onSearch = {
                    focusManager.clearFocus()
                    onSearch.invoke(searchText)
                },
                placeholder = { Text("Search Title") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                    )
                },
                trailingIcon = {
                    if (searchText.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    searchText = ""
                                    if (!expanded) {
                                        onSearchTextChange.invoke(searchText)
                                        onSearch.invoke(searchText)
                                    }
                                },
                        )
                    }
                },
                expanded = false,
                onExpandedChange = {},
            )
        },
        expanded = expanded,
        onExpandedChange = {},
        modifier = modifier.onFocusChanged {
            expanded = it.hasFocus
        },
    ) {
        LazyColumn(
            modifier = Modifier.wrapContentHeight(),
        ) {
            items(searchSuggestList) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            searchText = it
                            focusManager.clearFocus()
                            onSearch.invoke(searchText)
                        },
                ) {
                    Text(
                        text = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview_SearchText() {
    SumireTheme {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        ) {
            SearchText(
                modifier = Modifier.fillMaxWidth(),
                searchSuggestList = listOf(),
                onSearchTextChange = {},
                onSearch = { },
            )
        }
    }
}
