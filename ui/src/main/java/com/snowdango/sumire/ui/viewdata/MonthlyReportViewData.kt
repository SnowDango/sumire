package com.snowdango.sumire.ui.viewdata

import kotlin.time.Duration

data class MonthlyReportViewData(
    val yearMonthText: String,
    val playCount: Int,
    val songCount: Int,
    val artistCount: Int,
    // 再生時間を記録し始める前の再生の分は含まない
    val listeningTime: Duration,
    // listeningTime に入っていない、再生時間を記録し始める前の再生の回数
    val unmeasuredPlayCount: Int,
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
