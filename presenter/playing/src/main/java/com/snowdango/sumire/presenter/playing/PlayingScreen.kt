package com.snowdango.sumire.presenter.playing

import android.graphics.Bitmap
import android.provider.MediaStore.Audio.Albums
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.snowdango.sumire.ui.component.CircleSongArtwork
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayingScreen(){
    val viewModel: PlayingViewModel = koinViewModel()
    val currentSong = viewModel.currentPlayingSong.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        currentSong.value?.let {
            if(it.isActive) {
                item {
                    PlayingSongComponent(
                        artwork = it.songData.artwork,
                        title = it.songData.title,
                        album = it.songData.album,
                        artist = it.songData.artist,
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
){
    Column {
        CircleSongArtwork(
            modifier = Modifier
                .padding(top = 32.dp),
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
    }
}