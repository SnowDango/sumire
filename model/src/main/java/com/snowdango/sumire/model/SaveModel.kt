package com.snowdango.sumire.model

import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.entity.db.AppSongKey
import com.snowdango.sumire.data.entity.playing.PlayingSongData
import com.snowdango.sumire.data.entity.songlink.SongLinkData
import com.snowdango.sumire.data.entity.songlink.SongLinkResponse
import com.snowdango.sumire.data.util.toBase64
import com.snowdango.sumire.usecase.api.SongLinkApiUseCase
import com.snowdango.sumire.usecase.db.AlbumsUseCase
import com.snowdango.sumire.usecase.db.AppSongKeyUseCase
import com.snowdango.sumire.usecase.db.ArtistsUseCase
import com.snowdango.sumire.usecase.db.HistoriesUseCase
import com.snowdango.sumire.usecase.db.SongsUseCase
import com.snowdango.sumire.usecase.db.TasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SaveModel : KoinComponent {

    private val songLinkApiUseCase: SongLinkApiUseCase by inject()
    private val appSongKeyUseCase: AppSongKeyUseCase by inject()
    private val historiesUseCase: HistoriesUseCase by inject()
    private val artistsUseCase: ArtistsUseCase by inject()
    private val albumsUseCase: AlbumsUseCase by inject()
    private val songsUseCase: SongsUseCase by inject()
    private val tasksUseCase: TasksUseCase by inject()

    suspend fun saveSong(playingSongData: PlayingSongData) {
        val response = songLinkApiUseCase.getSongLinkData(
            playingSongData.songData.mediaId, playingSongData.songData.app
        )
        val mediaId: String = playingSongData.songData.mediaId
        val app: MusicApp = playingSongData.songData.app
        val songId: Long = appSongKeyUseCase.getSongIdByKey(mediaId, app)
        if (response.status == SongLinkResponse.Status.OK) {
            saveWithApi(songId, response.songData, playingSongData)
        } else {
            saveNoApi(songId, playingSongData, response.status)
        }
    }

    private suspend fun saveWithApi(
        songId: Long,
        songLinkData: SongLinkData,
        playingSongData: PlayingSongData
    ) {
        val keyMap: Map<MusicApp, String> = songLinkData.entities.filter {
            MusicApp.entries.find { app -> app.apiProvider == it.value.provider } != null
        }.map {
            MusicApp.entries.first { app -> app.apiProvider == it.value.provider } to
                    it.value.id
        }.toMap()
        val urlMap: Map<MusicApp, String> = songLinkData.links.filter {
            MusicApp.entries.find { app -> app.platform == it.key } != null
        }.map {
            MusicApp.entries.first { app -> app.platform == it.key } to
                    it.value.url
        }.toMap()

        withContext(Dispatchers.IO) {
            if (songId != -1L) {
                saveHistory(songId, playingSongData.playTime, playingSongData.songData.app)
                checkAppSongKey(songId, keyMap, urlMap)
            } else {
                saveData(
                    artist = playingSongData.songData.artist,
                    albumName = playingSongData.songData.album,
                    thumbnail = songLinkData.entities.values.first().thumbnailUrl,
                    isThumbUrl = true,
                    title = playingSongData.songData.title,
                    url = songLinkData.pageUrl,
                    playTime = playingSongData.playTime,
                    mapKey = keyMap,
                    mapUrl = urlMap,
                    status = SongLinkResponse.Status.OK,
                    mediaId = playingSongData.songData.mediaId,
                    app = playingSongData.songData.app
                )
            }
        }
    }

    private suspend fun saveNoApi(
        songId: Long,
        playingSongData: PlayingSongData,
        status: SongLinkResponse.Status
    ) {
        val keyMap: Map<MusicApp, String> =
            mapOf(playingSongData.songData.app to playingSongData.songData.mediaId)
        withContext(Dispatchers.IO) {
            if (songId != -1L) {
                saveHistory(songId, playingSongData.playTime, playingSongData.songData.app)
                checkAppSongKey(
                    songId,
                    keyMap,
                    mapOf(),
                )
            } else {
                saveData(
                    artist = playingSongData.songData.artist,
                    albumName = playingSongData.songData.album,
                    thumbnail = playingSongData.songData.artwork?.toBase64(),
                    isThumbUrl = false,
                    title = playingSongData.songData.title,
                    playTime = playingSongData.playTime,
                    mapKey = keyMap,
                    mapUrl = mapOf(),
                    status = status,
                    mediaId = playingSongData.songData.mediaId,
                    app = playingSongData.songData.app,
                )
            }
        }
    }

    private suspend fun saveData(
        artist: String,
        albumName: String,
        thumbnail: String?,
        isThumbUrl: Boolean,
        title: String,
        url: String? = null,
        playTime: LocalDateTime,
        mapKey: Map<MusicApp, String>,
        mapUrl: Map<MusicApp, String?>,
        status: SongLinkResponse.Status,
        mediaId: String,
        app: MusicApp,
    ) {
        val artistId: Long = saveArtist(artist)
        val albumId: Long = saveAlbum(artistId, albumName, thumbnail, isThumbUrl)
        val songId = saveSong(title, artistId, albumId, url)
        saveHistory(songId, playTime, app)
        checkAppSongKey(
            songId = songId,
            mapKey,
            mapUrl,
        )
        if (status == SongLinkResponse.Status.Error) {
            saveTasks(songId, mediaId)
        }

    }

    private suspend fun saveArtist(artist: String): Long {
        val artistId = artistsUseCase.getIdByName(artist)
        return if (artistId != -1L) {
            artistId
        } else {
            artistsUseCase.saveArtist(artist)
        }
    }

    private suspend fun saveAlbum(
        artistId: Long,
        albumName: String,
        thumbnail: String?,
        isThumbUrl: Boolean
    ): Long {
        val albumId = albumsUseCase.getIdByNameAndArtistId(albumName, artistId)
        return if (albumId != -1L) {
            albumId
        } else {
            albumsUseCase.saveAlbums(albumName, artistId, thumbnail, isThumbUrl)
        }
    }

    private suspend fun saveSong(title: String, artistId: Long, albumId: Long, url: String?): Long {
        return songsUseCase.saveSong(title, artistId, albumId, url)
    }

    private suspend fun saveHistory(songId: Long, playTime: LocalDateTime, app: MusicApp) {
        historiesUseCase.saveHistories(songId, playTime, app)
    }

    private suspend fun checkAppSongKey(
        songId: Long,
        keyMap: Map<MusicApp, String>,
        urlMap: Map<MusicApp, String?>
    ) {
        val result = appSongKeyUseCase.getBySongId(songId)
        val addKeyMap = keyMap.filter {
            result.find { appSongKey: AppSongKey ->
                it.key == appSongKey.app &&
                        it.value == appSongKey.mediaKey
            } == null
        }
        if (addKeyMap.isNotEmpty()) {
            appSongKeyUseCase.insertAll(songId, keyMap, urlMap)
        }
    }

    private suspend fun saveTasks(songId: Long, mediaId: String) {
        tasksUseCase.saveTasks(mediaId, songId)
    }
}