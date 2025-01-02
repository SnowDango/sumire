package com.snowdango.sumire.ui.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.snowdango.sumire.ui.theme.SumireTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchText(
    modifier: Modifier,
    onSearch: (searchText: String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf( false ) }
    val focusManager = LocalFocusManager.current
    SearchBar(
        inputField = {
            OutlinedTextField(
                value = searchText,
                onValueChange = { newValue -> searchText = newValue },
                placeholder = { Text("Search Title") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                ),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                    onSearch.invoke(searchText)
                })
            )
        },
        expanded = expanded,
        onExpandedChange = { newValue ->
            expanded = newValue
        },
        modifier = modifier,
    ) {

    }
}

@Preview
@Composable
fun Preview_SearchText() {
    SumireTheme {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            SearchText(
                modifier = Modifier.fillMaxWidth(),
                onSearch = { }
            )
        }
    }
}