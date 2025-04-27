package com.snowdango.sumire.settings

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.snowdango.sumire.data.entity.preference.UrlPriorityPlatform
import com.snowdango.sumire.presenter.settings.BuildConfig
import com.snowdango.sumire.presenter.settings.R
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
            },
        )
    }
    if (isUrlPriorityPlatformDialogShow) {
        UrlPriorityPlatformDialog(
            onDismissRequest = {
                isUrlPriorityPlatformDialogShow = false
            },
            onSelect = {
                it?.let { platform -> viewModel.setUrlPlatform(platform) }
            },
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        item {
            MainSettings(
                widgetStateText = datastore.value?.widgetActionType?.description ?: "",
                urlPrimaryServiceText = datastore.value?.urlPlatform?.platform
                    ?: UrlPriorityPlatform.SONG_LINK.platform,
                widgetOnClick = {
                    isWidgetDialogShow = true
                },
                urlPrimaryServiceClick = {
                    isUrlPriorityPlatformDialogShow = true
                },
            )
        }
        if (BuildConfig.DEBUG) {
            item {
                DebugSettings(
                    showkaseClick = {
                        onShowkaseIntent.invoke()
                    },
                )
            }
        }
    }
}

@Composable
fun MainSettings(
    widgetStateText: String,
    urlPrimaryServiceText: String,
    widgetOnClick: () -> Unit,
    urlPrimaryServiceClick: () -> Unit,
) {
    SettingsGroup(
        modifier = Modifier.padding(top = 32.dp),
        title = { Text(text = stringResource(R.string.main_settings_title)) },
    ) {
        SettingsMenuLink(
            title = { Text(stringResource(R.string.widget_type_setting_title)) },
            subtitle = { Text(text = widgetStateText) },
            onClick = widgetOnClick,
        )
        SettingsMenuLink(
            title = { Text(stringResource(R.string.url_primary_service_setting_title)) },
            subtitle = { Text(text = urlPrimaryServiceText) },
            onClick = urlPrimaryServiceClick,
        )
        SettingsMenuLink(
            enabled = false,
            title = { Text(stringResource(R.string.version_title)) },
            subtitle = { Text(BuildConfig.VERSION_NAME) },
            onClick = {},
        )
    }
}

@Composable
fun DebugSettings(
    showkaseClick: () -> Unit,
) {
    SettingsGroup(
        title = { Text(text = stringResource(R.string.debug_settings_title)) },
    ) {
        SettingsMenuLink(
            title = { Text(text = stringResource(R.string.showkase_show_setting_title)) },
            subtitle = { Text(text = stringResource(R.string.showkase_show_setting_subtitle)) },
            onClick = showkaseClick,
        )
        SettingsMenuLink(
            title = { Text(text = stringResource(R.string.crashlytics_crash_setting_title)) },
            subtitle = { Text(text = stringResource(R.string.crashlytics_crash_setting_subtitle)) },
            onClick = {
                throw RuntimeException("意図的なCrash")
            },
        )
    }
}

@Preview(group = SETTING_GROUP, name = "SettingsMenuLink")
@Composable
fun Preview_SettingsMenuLink() {
    SumireTheme {
        SettingsMenuLink(
            title = { Text("MenuLink") },
            subtitle = { Text("sub title") },
            onClick = {},
        )
    }
}

@Preview(group = SETTING_GROUP, name = "SettingGroup")
@Composable
fun Preview_SettingGroup() {
    SumireTheme {
        SettingsGroup(
            title = { Text(text = "SettingGroup") },
        ) { }
    }
}
