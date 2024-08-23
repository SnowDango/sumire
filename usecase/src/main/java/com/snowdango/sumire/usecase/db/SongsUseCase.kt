package com.snowdango.sumire.usecase.db


import com.snowdango.sumire.data.entity.db.Songs
import com.snowdango.sumire.repository.SongsDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SongsUseCase : KoinComponent {

    private val songsDatabase: SongsDatabase by inject()

    suspend fun saveSong(
        title: String,
        artistId: Long,
        albumId: Long,
    ): Long {
        val songs = Songs(
            title = title,
            artistId = artistId,
            albumId = albumId,
        )
        return songsDatabase.songsDao.insert(songs)

    }

}