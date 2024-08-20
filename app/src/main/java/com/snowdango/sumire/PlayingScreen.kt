package com.snowdango.sumire

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import com.snowdango.sumire.ui.component.CircleSongArtwork

@Composable
fun PlayingScreen(
    songSharedFlow: PlayingSongSharedFlow
){
    val playingSong = songSharedFlow.playingSongFlow.collectAsState(initial = null)
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        playingSong.value?.let {
            if(it.isActive) {
                CircleSongArtwork(
                    modifier = Modifier
                        .padding(top = 32.dp),
                    bitmap = it.songData.artwork
                )
            }
        }
    }
}