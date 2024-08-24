package com.snowdango.sumire.ui.viewdata

import com.snowdango.sumire.data.entity.MusicApp

data class SongCardViewData(
    val title: String,
    val artistName: String,
    val albumName: String,
    val thumbnail: String?,
    val isThumbUrl: Boolean,
    val playTimeText: String,
    val app: MusicApp,
)
