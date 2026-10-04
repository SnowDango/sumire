package com.snowdango.sumire.usecase.db

import androidx.paging.PagingSource
import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.entity.db.Histories
import com.snowdango.sumire.data.entity.db.relations.HistorySong
import com.snowdango.sumire.data.entity.db.report.ArtistPlayCount
import com.snowdango.sumire.data.entity.db.report.PlaySummary
import com.snowdango.sumire.data.entity.db.report.SongPlayCount
import com.snowdango.sumire.repository.SongsDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HistoriesUseCase : KoinComponent {

    private val songsDatabase: SongsDatabase by inject()

    /**
     * @return 追加した履歴の ID。あとから再生時間を書き込むときに使う
     */
    suspend fun saveHistories(songId: Long, playTime: LocalDateTime, app: MusicApp): Long {
        val history = Histories(
            songId = songId,
            playTime = playTime,
            app = app,
        )
        return songsDatabase.historiesDao.insert(history)
    }

    suspend fun addListeningTime(historyId: Long, listeningMs: Long) {
        songsDatabase.historiesDao.addListeningTime(historyId, listeningMs)
    }

    fun getHistoriesSongRecent(size: Long): Flow<List<HistorySong>> {
        return songsDatabase.historiesDao.getHistoriesSongRecent(size)
    }

    fun getPagingHistorySongs(): PagingSource<Int, HistorySong> {
        return songsDatabase.historiesDao.getPagingHistorySongs()
    }

    fun getPagingSearchHistoriesSongs(text: String): PagingSource<Int, HistorySong> {
        return songsDatabase.historiesDao.getPagingSearchHistorySong(text)
    }

    fun getPlaySummary(startInclusive: LocalDateTime, endExclusive: LocalDateTime): Flow<PlaySummary> {
        return songsDatabase.historiesDao.getPlaySummary(startInclusive, endExclusive)
    }

    fun getTopSongs(startInclusive: LocalDateTime, endExclusive: LocalDateTime, limit: Int): Flow<List<SongPlayCount>> {
        return songsDatabase.historiesDao.getTopSongs(startInclusive, endExclusive, limit)
    }

    fun getTopArtists(
        startInclusive: LocalDateTime,
        endExclusive: LocalDateTime,
        limit: Int,
    ): Flow<List<ArtistPlayCount>> {
        return songsDatabase.historiesDao.getTopArtists(startInclusive, endExclusive, limit)
    }
}
