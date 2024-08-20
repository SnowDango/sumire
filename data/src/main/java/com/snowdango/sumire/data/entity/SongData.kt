package com.snowdango.sumire.data.entity

import android.graphics.Bitmap

data class SongData(
    val title: String,
    val artist: String,
    val album: String,
    val app: MusicApp,
    val artwork: Bitmap,
)
