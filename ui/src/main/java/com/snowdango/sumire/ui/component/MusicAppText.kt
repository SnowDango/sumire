package com.snowdango.sumire.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.ui.UTIL_GROUP
import com.snowdango.sumire.ui.theme.SumireTheme

@Composable
fun MusicAppText(
    app: MusicApp,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    val appString = when (app) {
        MusicApp.APPLE_MUSIC -> "AppleMusic"
        MusicApp.AMAZON -> "AmazonMusic"
        MusicApp.DEEZER -> "Deezer"
        MusicApp.TIDAL -> "Tidal"
        MusicApp.YANDEX -> "Yandex"
        MusicApp.GOOGLE -> "GoogleMusic"
        MusicApp.YOUTUBE -> "YouTubeMusic"
        MusicApp.SPOTIFY -> "Spotify"
        MusicApp.PANDORA -> "Pandora"
        MusicApp.NAPSTER -> "Napster"
    }
    Text(
        text = appString,
        style = style,
        modifier = modifier,
    )
}

@Preview(group = UTIL_GROUP, name = "MusicAppText")
@Composable
fun PreviewMusicAppText(
    @PreviewParameter(provider = MusicAppPramProvider::class) data: MusicApp,
) {
    SumireTheme {
        MusicAppText(
            app = data,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

class MusicAppPramProvider : PreviewParameterProvider<MusicApp> {
    override val values: Sequence<MusicApp>
        get() = MusicApp.entries.asSequence()
}
