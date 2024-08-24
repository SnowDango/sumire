package com.snowdango.sumire.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.ui.R

@Composable
fun MusicAppImage(
    app: MusicApp,
    modifier: Modifier = Modifier
){
    val musicAppIcon = when (app) {
        MusicApp.APPLE_MUSIC -> R.drawable.apple_music
        MusicApp.SPOTIFY -> R.drawable.spotify
        MusicApp.YOUTUBE -> R.drawable.youtube_music
        MusicApp.GOOGLE -> R.drawable.google_music
        MusicApp.AMAZON -> R.drawable.amazon_music
        MusicApp.TIDAL -> R.drawable.tidal
        MusicApp.DEEZER -> R.drawable.deezer
        MusicApp.YANDEX -> R.drawable.yandex
        MusicApp.NAPSTER -> R.drawable.napster
        MusicApp.PANDORA -> R.drawable.pandora
    }
    Image(
        painter = painterResource(id = musicAppIcon),
        contentDescription = null,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
    )
}