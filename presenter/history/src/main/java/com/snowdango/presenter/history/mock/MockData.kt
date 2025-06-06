package com.snowdango.presenter.history.mock

import androidx.paging.PagingData
import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.ui.viewdata.SongCardViewData
import kotlinx.coroutines.flow.MutableStateFlow

object MockData {
    const val mockDate = "2023/7/20"

    private val mockHistories = listOf(
        SongCardViewData(
            title = "title",
            artistName = "artist",
            albumName = "album",
            thumbnail = "",
            isThumbUrl = true,
            playTimeText = "20:33:20",
            headerDay = "2023/7/20",
            app = MusicApp.APPLE_MUSIC,
        ),
        SongCardViewData(
            title = "title_title",
            artistName = "artist_artist",
            albumName = "album_album",
            thumbnail = "",
            isThumbUrl = true,
            playTimeText = "20:32:20",
            headerDay = "2023/7/20",
            app = MusicApp.APPLE_MUSIC,
        ),
        SongCardViewData(
            title = "title_title_title",
            artistName = "artist_artist_artist",
            albumName = "album_album_album",
            thumbnail = "",
            isThumbUrl = true,
            playTimeText = "20:31:20",
            headerDay = "2023/7/20",
            app = MusicApp.APPLE_MUSIC,
        ),
        SongCardViewData(
            title = "title",
            artistName = "artist",
            albumName = "album",
            thumbnail = "",
            isThumbUrl = true,
            playTimeText = "20:33:20",
            headerDay = "2023/7/19",
            app = MusicApp.APPLE_MUSIC,
        ),
        SongCardViewData(
            title = "title_title",
            artistName = "artist_artist",
            albumName = "album_album",
            thumbnail = "",
            isThumbUrl = true,
            playTimeText = "20:32:20",
            headerDay = "2023/7/19",
            app = MusicApp.APPLE_MUSIC,
        ),
        SongCardViewData(
            title = "title_title_title",
            artistName = "artist_artist_artist",
            albumName = "album_album_album",
            thumbnail = "",
            isThumbUrl = true,
            playTimeText = "20:31:20",
            headerDay = "2023/7/19",
            app = MusicApp.APPLE_MUSIC,
        ),
    )

    val mockPagingFlow = MutableStateFlow(
        PagingData.from(
            mockHistories,
        ),
    )
}
