package com.snowdango.sumire.usecase.db

import com.snowdango.sumire.data.entity.db.Albums
import com.snowdango.sumire.repository.SongsDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlbumsUseCase : KoinComponent {

    private val songsDatabase: SongsDatabase by inject()

    suspend fun getIdByNameAndArtistId(albumName: String, artistId: Long): Long {
        return songsDatabase.albumsDao.getIdByNameAndArtistId(albumName, artistId) ?: -1
    }

    suspend fun saveAlbums(
        name: String,
        artistId: Long,
        thumbnail: String?,
        isThumbUrl: Boolean,
    ): Long {
        val album = Albums(
            name = name,
            artistId = artistId,
            thumbnail = thumbnail,
            isThumbUrl = isThumbUrl,
        )
        return songsDatabase.albumsDao.insert(album)
    }
}
