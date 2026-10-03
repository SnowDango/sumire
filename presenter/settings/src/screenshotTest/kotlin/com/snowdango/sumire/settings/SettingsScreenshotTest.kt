package com.snowdango.sumire.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

// Compose Preview Screenshot Testing は screenshotTest source set にある @PreviewTest の
// Preview だけを撮影する。中身は main の Preview を呼び出し、@Preview のパラメータは
// main 側と揃えておくこと。

@PreviewTest
@Preview(group = SETTING_GROUP, name = "SettingsMenuLink")
@Composable
fun SettingsMenuLinkPreviewTest() {
    Preview_SettingsMenuLink()
}

@PreviewTest
@Preview(group = SETTING_GROUP, name = "SettingGroup")
@Composable
fun SettingGroupPreviewTest() {
    Preview_SettingGroup()
}
