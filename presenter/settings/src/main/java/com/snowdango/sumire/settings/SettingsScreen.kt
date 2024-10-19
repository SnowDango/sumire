package com.snowdango.sumire.settings

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.snowdango.sumire.data.entity.preference.UrlPriorityPlatform
import com.snowdango.sumire.settings.component.UrlPriorityPlatformDialog
import com.snowdango.sumire.settings.component.WidgetActionTypeDialog
import com.snowdango.sumire.ui.theme.SumireTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onShowkaseIntent: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = koinViewModel()

    val datastore = viewModel.settingsFlow.collectAsStateWithLifecycle(null)

    var isWidgetDialogShow by remember { mutableStateOf(false) }
    var isUrlPriorityPlatformDialogShow by remember { mutableStateOf(false) }

    if (isWidgetDialogShow) {
        WidgetActionTypeDialog(
            onDismissRequest = {
                isWidgetDialogShow = false
            },
            onSelect = {
                it?.let { type -> viewModel.setWidgetActionType(type) }
            }
        )
    }
    if (isUrlPriorityPlatformDialogShow) {
        UrlPriorityPlatformDialog(
            onDismissRequest = {
                isUrlPriorityPlatformDialogShow = false
            },
            onSelect = {
                it?.let { platform -> viewModel.setUrlPlatform(platform) }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            SettingsGroup(
                modifier = Modifier.padding(top = 32.dp),
                title = { Text(text = "Settings") }
            ) {
                SettingsMenuLink(
                    title = { Text("ウィジェットのタップ時の動作") },
                    subtitle = {
                        Text(
                            text = datastore.value?.widgetActionType?.description ?: ""
                        )
                    },
                    onClick = {
                        isWidgetDialogShow = true
                    }
                )
                SettingsMenuLink(
                    title = { Text("URL取得時に優先されるサービス") },
                    subtitle = {
                        Text(
                            text = datastore.value?.urlPlatform?.platform
                                ?: UrlPriorityPlatform.SONG_LINK.platform
                        )
                    },
                    onClick = {
                        isUrlPriorityPlatformDialogShow = true
                    }
                )
                SettingsMenuLink(
                    title = { Text("Versions") },
                    subtitle = { Text(Build.VERSION.RELEASE) },
                    onClick = {},
                )
            }
        }
        if (BuildConfig.DEBUG) {
            item {
                SettingsGroup(
                    title = { Text(text = "Dev") }
                ) {
                    SettingsMenuLink(
                        title = { Text(text = "showkaseの表示") },
                        subtitle = { Text(text = "UiCatalogの表示をします。") },
                        onClick = {
                            onShowkaseIntent.invoke()
                        }
                    )
                }
            }
        }
    }
}

@Preview(group = SETTING_GROUP, name = "SettingsMenuLink")
@Composable
fun PreviewSettingsMenuLink() {
    SumireTheme {
        SettingsGroup(
            title = { Text("SettingGroup") }
        ) {
            SettingsMenuLink(
                title = { Text("MenuLink") },
                subtitle = { Text("sub title") },
                onClick = {},
            )
        }
    }
}