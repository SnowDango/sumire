package com.snowdango.sumire.presenter.playing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.snowdango.sumire.ui.component.CircleSongArtwork
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayingScreen(){
    val viewModel: PlayingViewModel = koinViewModel()
    val currentSong = viewModel.currentPlayingSong.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        currentSong.value?.let {
            if(it.isActive) {
                CircleSongArtwork(
                    modifier = Modifier
                        .padding(top = 32.dp),
                    bitmap = it.songData.artwork
                )
                Text(
                    text = it.songData.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp),
                )
                Text(
                    text = it.songData.album,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = it.songData.artist,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}