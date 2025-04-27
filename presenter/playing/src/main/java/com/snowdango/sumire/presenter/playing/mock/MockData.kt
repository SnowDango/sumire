package com.snowdango.sumire.presenter.playing.mock

import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.entity.playing.PlayingSongData
import com.snowdango.sumire.data.entity.playing.SongData
import com.snowdango.sumire.ui.viewdata.SongCardViewData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object MockData {

    val currentSongMock: StateFlow<PlayingSongData?> = MutableStateFlow(
        PlayingSongData(
            songData = SongData(
                title = "title",
                artist = "artist",
                album = "album",
                app = MusicApp.APPLE_MUSIC,
                artwork = null,
                mediaId = "mediaId",
            ),
            isActive = true,
            playTime = Instant.fromEpochMilliseconds(1L).toLocalDateTime(TimeZone.currentSystemDefault()),
        ),
    )
    private val mockRecentHistories: List<SongCardViewData> = listOf(
        SongCardViewData(
            title = "title",
            artistName = "artist",
            albumName = "album",
            thumbnail = "",
            isThumbUrl = true,
            playTimeText = "2023/7/20-20:33:20",
            headerDay = "2023/7/20",
            app = MusicApp.APPLE_MUSIC,
        ),
        SongCardViewData(
            title = "title_title",
            artistName = "artist_artist",
            albumName = "album_album",
            thumbnail = "",
            isThumbUrl = true,
            playTimeText = "2023/7/20-20:32:20",
            headerDay = "2023/7/20",
            app = MusicApp.APPLE_MUSIC,
        ),
        SongCardViewData(
            title = "title_title_title",
            artistName = "artist_artist_artist",
            albumName = "album_album_album",
            thumbnail = "",
            isThumbUrl = true,
            playTimeText = "2023/7/20-20:31:20",
            headerDay = "2023/7/20",
            app = MusicApp.APPLE_MUSIC,
        ),
        SongCardViewData(
            title = "title",
            artistName = "artist",
            albumName = "album",
            thumbnail = "",
            isThumbUrl = true,
            playTimeText = "2023/7/19-20:33:20",
            headerDay = "2023/7/20",
            app = MusicApp.APPLE_MUSIC,
        ),
        SongCardViewData(
            title = "title_title",
            artistName = "artist_artist",
            albumName = "album_album",
            thumbnail = "",
            isThumbUrl = true,
            playTimeText = "2023/7/19-20:32:20",
            headerDay = "2023/7/20",
            app = MusicApp.APPLE_MUSIC,
        ),
        SongCardViewData(
            title = "title_title_title",
            artistName = "artist_artist_artist",
            albumName = "album_album_album",
            thumbnail = "",
            isThumbUrl = true,
            playTimeText = "2023/7/19-20:31:20",
            headerDay = "2023/7/20",
            app = MusicApp.APPLE_MUSIC,
        ),
    )

    val recentHistories: StateFlow<List<SongCardViewData>> = MutableStateFlow(mockRecentHistories)
}
