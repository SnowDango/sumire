package com.snowdango.sumire.settings.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.alorma.compose.settings.ui.SettingsRadioButton
import com.snowdango.sumire.data.entity.preference.UrlPriorityPlatform


@Composable
fun UrlPriorityPlatformDialog(
    onDismissRequest: () -> Unit,
    onSelect: (platform: String?) -> Unit,
) {
    var selectPlatform: String? by remember { mutableStateOf(null) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = selectPlatform != null,
                onClick = {
                    onSelect.invoke(selectPlatform)
                    onDismissRequest.invoke()
                }
            ) {
                Text("Select")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest.invoke()
            }) {
                Text("Cancel")
            }
        },
        title = {
            Text(text = "サービスの選択")
        },
        text = {
            LazyColumn {
                items(UrlPriorityPlatform.entries) {
                    SettingsRadioButton(
                        state = it.platform == selectPlatform,
                        title = { Text(it.platform) },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        selectPlatform = it.platform
                    }
                }
            }
        }
    )

}