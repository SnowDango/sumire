package com.snowdango.sumire.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.android.tools.screenshot.PreviewTest
import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.ui.UTIL_GROUP

// Compose Preview Screenshot Testing は screenshotTest source set にある @PreviewTest の
// Preview だけを撮影する。中身は main の Preview を呼び出し、@Preview のパラメータは
// main 側と揃えておくこと。

@PreviewTest
@Preview
@Composable
fun SearchTextPreviewTest() {
    Preview_SearchText()
}

@PreviewTest
@Preview(group = UTIL_GROUP, name = "MusicAppImage")
@Composable
fun MusicAppImagePreviewTest(
    @PreviewParameter(MusicAppPramProvider::class) data: MusicApp,
) {
    PreviewMusicAppImage(data)
}

@PreviewTest
@Preview(group = UTIL_GROUP, name = "CircleSongArtwork")
@Composable
fun CircleSongArtworkPreviewTest() {
    PreviewCircleSongArtwork()
}

@PreviewTest
@Preview(group = UTIL_GROUP, name = "ListSongCard")
@Composable
fun ListSongCardPreviewTest() {
    PreviewListSongCard()
}

@PreviewTest
@Preview(group = UTIL_GROUP, name = "MusicAppText")
@Composable
fun MusicAppTextPreviewTest(
    @PreviewParameter(MusicAppPramProvider::class) data: MusicApp,
) {
    PreviewMusicAppText(data)
}

@PreviewTest
@Preview(group = UTIL_GROUP, name = "CircleLoading")
@Composable
fun CircleLoadingPreviewTest() {
    CircleLoading()
}
