package com.snowdango.sumire.usecase.db


import com.snowdango.sumire.data.entity.db.Histories
import com.snowdango.sumire.repository.SongsDatabase
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HistoriesUseCase : KoinComponent {

    private val songsDatabase: SongsDatabase by inject()

    suspend fun saveHistories(songId: Long, playTime: LocalDateTime) {
        val history = Histories(
            songId = songId,
            playTime = playTime,
        )
        songsDatabase.historiesDao.insert(history)
    }
}