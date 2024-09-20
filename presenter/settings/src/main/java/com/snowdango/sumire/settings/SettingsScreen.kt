package com.snowdango.sumire.settings

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.android.showkase.models.Showkase
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.snowdango.sumire.ui.theme.SumireTheme

@Composable
fun SettingsScreen(

) {
    val context = LocalContext.current
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
                    title = { Text("Versions") },
                    subtitle = { Text(Build.VERSION.CODENAME) },
                    onClick = {},
                )
            }
        }
        item {
            SettingsGroup(
                title = { Text(text = "Dev") }
            ) {
                SettingsMenuLink(
                    title = { Text(text = "showkaseの表示") },
                    subtitle = { Text(text = "UiCatalogの表示をします。") },
                    onClick = {
                        val intent = Showkase.getBrowserIntent(context)
                        context.startActivity(intent)
                    }
                )
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