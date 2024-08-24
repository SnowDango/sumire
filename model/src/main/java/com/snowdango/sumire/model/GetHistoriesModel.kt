package com.snowdango.sumire.model

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
                SongCardViewData(
                    title = data.song.songs.title,
                    artistName = data.song.artists.name,
                    albumName = data.song.albums.name,
                    thumbnail = data.song.albums.thumbnail,
                    isThumbUrl = data.song.albums.isThumbUrl,
                    playTimeText = data.history.playTime.toFormatDateTime(),
                    app = data.history.app,
                )
            }
        }
    }

}