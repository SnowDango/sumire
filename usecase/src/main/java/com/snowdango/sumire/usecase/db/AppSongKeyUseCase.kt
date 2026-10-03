package com.snowdango.sumire.usecase.db

import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.entity.db.AppSongKey
import com.snowdango.sumire.data.entity.db.relations.SongAppKeys
import com.snowdango.sumire.repository.SongsDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppSongKeyUseCase : KoinComponent {

    private val songsDatabase: SongsDatabase by inject()

    suspend fun getBySongId(songId: Long): List<AppSongKey> {
        return songsDatabase.appSongKeyDao.getBySongId(songId)
    }

    suspend fun getAppSongKeys(mediaId: String, app: MusicApp): SongAppKeys? {
        return songsDatabase.appSongKeyDao.getAppKeys(mediaId, app)
    }

    suspend fun insertAll(
        songId: Long,
        keyMap: Map<MusicApp, String>,
        urlMap: Map<MusicApp, String?>,
    ): List<Long> {
        val appSongKeys = keyMap.map {
            AppSongKey(
                songId = songId,
                app = it.key,
                mediaKey = it.value,
                url = urlMap[it.key],
            )
        }
        return songsDatabase.appSongKeyDao.insert(appSongKeys)
    }
}
