package com.snowdango.sumire.presenter.playing

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.entity.playing.PlayingSongData
import com.snowdango.sumire.ui.component.CircleSongArtwork
import com.snowdango.sumire.ui.component.ListSongCard
import com.snowdango.sumire.ui.component.MusicAppImage
import com.snowdango.sumire.ui.component.MusicAppText
import com.snowdango.sumire.ui.theme.SumireTheme
import com.snowdango.sumire.ui.viewdata.SongCardViewData
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayingScreen(
    windowSize: WindowSizeClass
) {
    val viewModel: PlayingViewModel = koinViewModel()
    val currentSong = viewModel.currentPlayingSong.collectAsStateWithLifecycle()
    val recentHistories = viewModel.recentHistories.collectAsStateWithLifecycle()
    val isNowPlaying = currentSong.value?.isActive ?: false
    if (isNowPlaying) {
        val isLandScape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        if(!isLandScape)
        when (windowSize.widthSizeClass) {
            WindowWidthSizeClass.Compact -> PlayingCompactScreen(
                currentSong = currentSong,
                recentHistories = recentHistories
            )
            WindowWidthSizeClass.Medium -> PlayingSplit2Screen(
                currentSong = currentSong,
                recentHistories = recentHistories,
            )
            WindowWidthSizeClass.Expanded -> PlayingSplit2Screen(
                currentSong = currentSong,
                recentHistories = recentHistories,
            )
        }else{
            PlayingSplit2Screen(
                currentSong = currentSong,
                recentHistories = recentHistories,
            )
        }
    } else {
        NothingPlayingSongComponent()
    }
}

// 縦画面用
@Composable
fun PlayingCompactScreen(
    currentSong: State<PlayingSongData?>,
    recentHistories: State<List<SongCardViewData>>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        currentSong.value?.let {
            item {
                PlayingSongComponent(
                    artwork = it.songData.artwork,
                    title = it.songData.title,
                    album = it.songData.album,
                    artist = it.songData.artist,
                    app = it.songData.app,
                )
            }
        }
        item {
            Text(
                text = "Recent",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(start = 32.dp, top = 32.dp, end = 32.dp, bottom = 16.dp)
                    .fillMaxWidth()
            )
        }
        items(recentHistories.value) {
            ListSongCard(
                songCardViewData = it,
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, bottom = 4.dp)
            )
        }
    }
}

// 横画面とFold用
@Composable
fun PlayingSplit2Screen(
    currentSong: State<PlayingSongData?>,
    recentHistories: State<List<SongCardViewData>>
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            currentSong.value?.let {
                PlayingSongComponent(
                    artwork = it.songData.artwork,
                    title = it.songData.title,
                    album = it.songData.album,
                    artist = it.songData.artist,
                    app = it.songData.app,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "Recent",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(start = 32.dp, top = 32.dp, end = 32.dp, bottom = 16.dp)
                            .fillMaxWidth()
                    )
                }
                items(recentHistories.value) {
                    ListSongCard(
                        songCardViewData = it,
                        modifier = Modifier
                            .padding(start = 32.dp, end = 32.dp, bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlayingSongComponent(
    artwork: Bitmap?,
    title: String,
    album: String,
    artist: String,
    app: MusicApp,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        CircleSongArtwork(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(0.5f)
                .aspectRatio(1f),
            bitmap = artwork
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(0.5f),
        )
        Text(
            text = album,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(0.5f),
        )
        Text(
            text = artist,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(0.5f),
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "from",
                style = MaterialTheme.typography.labelSmall
            )
            MusicAppImage(
                app = app,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(16.dp)
            )
            MusicAppText(
                app = app,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Preview
@Composable
fun PreviewPlayingSongComponent() {
    SumireTheme {
        PlayingSongComponent(
            artwork = null,
            title = "title2",
            album = "album",
            artist = "artist",
            app = MusicApp.APPLE_MUSIC
        )
    }
}

@Preview
@Composable
fun NothingPlayingSongComponent() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Music is not playing \nin all apps.",
            style = TextStyle(
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                lineHeight = 32.sp,
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center

        )
    }
}