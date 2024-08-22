package com.snowdango.sumire.data.entity.playing

import android.graphics.Bitmap
import com.snowdango.sumire.data.entity.MusicApp

data class SongData(
    val title: String,
    val artist: String,
    val album: String,
    val app: MusicApp,
    val artwork: Bitmap?,
    val mediaId: String,
)
