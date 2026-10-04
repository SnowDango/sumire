package com.snowdango.presenter.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

// Compose Preview Screenshot Testing は screenshotTest source set にある @PreviewTest の
// Preview だけを撮影する。中身は main の Preview を呼び出し、@Preview のパラメータは
// main 側と揃えておくこと。

@PreviewTest
@Preview(group = HISTORY_GROUP, name = "DateHeader")
@Composable
fun DateHeaderPreviewTest() {
    Preview_DateHeader()
}

@PreviewTest
@Preview(group = HISTORY_GROUP, name = "HistoryCompatScreen")
@Composable
fun HistoryCompatScreenPreviewTest() {
    Preview_HistoryCompatScreen()
}

@PreviewTest
@Preview(
    group = HISTORY_GROUP,
    name = "HistorySplit2Screen",
    device = "spec:width=1280dp,height=800dp,dpi=240",
)
@Composable
fun HistorySplit2ScreenPreviewTest() {
    Preview_HistorySplit2Screen()
}
