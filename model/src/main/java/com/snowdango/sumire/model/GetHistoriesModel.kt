package com.snowdango.sumire.model

import androidx.paging.PagingSource
import com.snowdango.sumire.data.entity.db.relations.HistorySong
import com.snowdango.sumire.data.util.toDate
import com.snowdango.sumire.data.util.toFormatDateTime
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
                    data
                )
            }
        }
    }

    fun getPagingHistorySongs(): PagingSource<Int, HistorySong> {
        return historiesUseCase.getPagingHistorySongs()
    }

    fun convertHistorySongToSongCardViewData(
        historySong: HistorySong
    ): SongCardViewData {
        return SongCardViewData(
            title = historySong.song.songs.title,
            artistName = historySong.song.artists.name,
            albumName = historySong.song.albums.name,
            thumbnail = historySong.song.albums.thumbnail,
            isThumbUrl = historySong.song.albums.isThumbUrl,
            playTimeText = historySong.history.playTime.toFormatDateTime(),
            headerDay = historySong.history.playTime.toDate(),
            app = historySong.history.app,
        )
    }

}