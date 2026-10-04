package com.snowdango.sumire.presenter.playing

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

// Compose Preview Screenshot Testing は screenshotTest source set にある @PreviewTest の
// Preview だけを撮影する。中身は main の Preview を呼び出し、@Preview のパラメータは
// main 側と揃えておくこと。

@PreviewTest
@Preview(group = PLAYING_GROUP, name = "NothingPlayingSong")
@Composable
fun NothingPlayingSongPreviewTest() {
    Preview_NothingPlayingSongComponent()
}

@PreviewTest
@Preview(group = PLAYING_GROUP, name = "PlayingSong")
@Composable
fun PlayingSongPreviewTest() {
    Preview_PlayingSongComponent()
}

@PreviewTest
@Preview(group = PLAYING_GROUP, name = "PlayingCompactScreen")
@Composable
fun PlayingCompactScreenPreviewTest() {
    Preview_PlayingCompactScreen()
}

@PreviewTest
@Preview(
    group = PLAYING_GROUP,
    name = "PlayingSplit2Screen",
    device = "spec:width=1280dp,height=800dp,dpi=240",
)
@Composable
fun PlayingSplit2ScreenPreviewTest() {
    Preview_PlayingSplit2Screen()
}
