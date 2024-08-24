package com.snowdango.sumire.ui.viewdata

data class SongCardViewData(
    val title: String,
    val artistName: String,
    val albumName: String,
    val thumbnail: String?,
    val isThumbUrl: Boolean,
    val playTimeText: String,
)
