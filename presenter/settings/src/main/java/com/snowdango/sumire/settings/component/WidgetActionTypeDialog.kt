package com.snowdango.sumire.settings.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.alorma.compose.settings.ui.SettingsRadioButton
import com.alorma.compose.settings.ui.base.internal.SettingsTileDefaults
import com.snowdango.sumire.data.entity.preference.WidgetActionType

@Composable
fun WidgetActionTypeDialog(
    onDismissRequest: () -> Unit,
    onSelect: (widgetActionType: WidgetActionType?) -> Unit,
) {
    var selectType: WidgetActionType? by remember { mutableStateOf(null) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = selectType != null,
                onClick = {
                    onSelect.invoke(selectType)
                    onDismissRequest.invoke()
                },
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
            Text(text = "ウィジェットをタップした時の動作")
        },
        text = {
            Column {
                WidgetActionType.entries.forEach {
                    SettingsRadioButton(
                        state = selectType == it,
                        title = {
                            Text(
                                text = it.description,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        },
                        colors = SettingsTileDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        ),
                    ) {
                        selectType = it
                    }
                }
            }
        },
    )
}
