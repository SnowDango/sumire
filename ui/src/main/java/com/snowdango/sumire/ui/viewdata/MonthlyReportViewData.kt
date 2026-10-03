package com.snowdango.sumire.ui.viewdata

data class MonthlyReportViewData(
    val yearMonthText: String,
    val playCount: Int,
    val songCount: Int,
    val artistCount: Int,
    val topSongs: List<RankedSongViewData>,
    val topArtists: List<RankedArtistViewData>,
)

data class RankedSongViewData(
    val rank: Int,
    val title: String,
    val artistName: String,
    val albumName: String,
    val thumbnail: String?,
    val isThumbUrl: Boolean,
    val playCount: Int,
)

data class RankedArtistViewData(
    val rank: Int,
    val name: String,
    val playCount: Int,
)
