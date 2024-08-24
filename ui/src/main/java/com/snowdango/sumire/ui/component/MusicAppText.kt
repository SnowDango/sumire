package com.snowdango.sumire.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.snowdango.sumire.data.entity.MusicApp

@Composable
fun MusicAppText(
    app: MusicApp,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    val appString = when(app) {
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