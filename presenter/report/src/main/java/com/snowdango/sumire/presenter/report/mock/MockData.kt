package com.snowdango.sumire.presenter.report.mock

import com.snowdango.sumire.ui.viewdata.MonthlyReportViewData
import com.snowdango.sumire.ui.viewdata.RankedArtistViewData
import com.snowdango.sumire.ui.viewdata.RankedSongViewData

object MockData {

    private val mockTopSongs = listOf(
        RankedSongViewData(
            rank = 1,
            title = "title",
            artistName = "artist",
            albumName = "album",
            thumbnail = null,
            isThumbUrl = false,
            playCount = 24,
        ),
        RankedSongViewData(
            rank = 2,
            title = "title_title_title_title_title_title_title",
            artistName = "artist_artist",
            albumName = "album_album",
            thumbnail = null,
            isThumbUrl = false,
            playCount = 12,
        ),
        RankedSongViewData(
            rank = 3,
            title = "title_title_title",
            artistName = "artist_artist_artist",
            albumName = "album_album_album",
            thumbnail = null,
            isThumbUrl = false,
            playCount = 8,
        ),
        RankedSongViewData(
            rank = 4,
            title = "title",
            artistName = "artist",
            albumName = "album",
            thumbnail = null,
            isThumbUrl = false,
            playCount = 3,
        ),
        RankedSongViewData(
            rank = 5,
            title = "title_title",
            artistName = "artist_artist",
            albumName = "album_album",
            thumbnail = null,
            isThumbUrl = false,
            playCount = 1,
        ),
    )

    private val mockTopArtists = listOf(
        RankedArtistViewData(
            rank = 1,
            name = "artist",
            playCount = 27,
        ),
        RankedArtistViewData(
            rank = 2,
            name = "artist_artist",
            playCount = 13,
        ),
        RankedArtistViewData(
            rank = 3,
            name = "artist_artist_artist_artist_artist_artist_artist",
            playCount = 8,
        ),
    )

    val report = MonthlyReportViewData(
        yearMonthText = "2023/07",
        playCount = 48,
        songCount = 5,
        artistCount = 3,
        topSongs = mockTopSongs,
        topArtists = mockTopArtists,
    )
}
