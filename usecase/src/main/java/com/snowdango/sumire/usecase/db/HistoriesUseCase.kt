package com.snowdango.sumire.usecase.db


import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.entity.db.Histories
import com.snowdango.sumire.data.entity.db.relations.HistorySong
import com.snowdango.sumire.repository.SongsDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HistoriesUseCase : KoinComponent {

    private val songsDatabase: SongsDatabase by inject()

    suspend fun saveHistories(songId: Long, playTime: LocalDateTime, app: MusicApp) {
        val history = Histories(
            songId = songId,
            playTime = playTime,
            app = app
        )
        songsDatabase.historiesDao.insert(history)
    }

    fun getHistoriesSongRecent(size: Long): Flow<List<HistorySong>> {
        return songsDatabase.historiesDao.getHistoriesSongRecent(size)
    }
}