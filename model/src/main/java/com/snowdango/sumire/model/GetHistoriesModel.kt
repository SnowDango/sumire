package com.snowdango.sumire.model

import androidx.paging.PagingSource
import com.snowdango.sumire.data.entity.db.relations.HistorySong
import com.snowdango.sumire.data.util.LocalDateTimeFormatType
import com.snowdango.sumire.data.util.toFormatString
import com.snowdango.sumire.ui.viewdata.SongCardViewData
import com.snowdango.sumire.usecase.db.HistoriesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetHistoriesModel : KoinComponent {

    private val historiesUseCase: HistoriesUseCase by inject()

    fun getRecentHistoriesSongFlow(size: Long): Flow<List<SongCardViewData>> {
        return historiesUseCase.getHistoriesSongRecent(size).map {
            it.map { data ->
                convertHistorySongToSongCardViewData(
                    data,
                    LocalDateTimeFormatType.FULL_DATE_TIME
                )
            }
        }
    }

    fun getPagingHistorySongs(): PagingSource<Int, HistorySong> {
        return historiesUseCase.getPagingHistorySongs()
    }

    fun getPagingSearchHistorySongs(text: String): PagingSource<Int, HistorySong> {
        return historiesUseCase.getPagingSearchHistoriesSongs(text)
    }

    fun convertHistorySongToSongCardViewData(
        historySong: HistorySong,
        type: LocalDateTimeFormatType,
    ): SongCardViewData {
        return SongCardViewData(
            title = historySong.song.songs.title,
            artistName = historySong.song.artists.name,
            albumName = historySong.song.albums.name,
            thumbnail = historySong.song.albums.thumbnail,
            isThumbUrl = historySong.song.albums.isThumbUrl,
            playTimeText = historySong.history.playTime.toFormatString(type),
            headerDay = historySong.history.playTime.toFormatString(LocalDateTimeFormatType.ONLY_DATE),
            app = historySong.history.app,
        )
    }

}