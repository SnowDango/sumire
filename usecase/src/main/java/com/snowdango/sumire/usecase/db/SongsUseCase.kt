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
        url: String?,
    ): Long {
        val songs = Songs(
            title = title,
            artistId = artistId,
            albumId = albumId,
            url = url,
        )
        return songsDatabase.songsDao.insert(songs)
    }

    suspend fun getSearchSongsList(searchText: String): List<Songs> {
        return songsDatabase.songsDao.getSearchTitle(searchText)
    }

}