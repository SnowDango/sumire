package com.snowdango.sumire.usecase.db

import com.snowdango.sumire.data.entity.db.Artists
import com.snowdango.sumire.repository.SongsDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ArtistsUseCase : KoinComponent {

    private val songsDatabase: SongsDatabase by inject()

    suspend fun saveArtist(artistName: String): Long {
        val artists = Artists(
            name = artistName,
        )
        return songsDatabase.artistsDao.insert(artists)
    }

    suspend fun getIdByName(artistName: String): Long {
        return songsDatabase.artistsDao.getArtist(artistName) ?: -1
    }
}
