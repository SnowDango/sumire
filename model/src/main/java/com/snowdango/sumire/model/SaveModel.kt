package com.snowdango.sumire.model

import android.util.Log
import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.entity.db.AppSongKey
import com.snowdango.sumire.data.entity.db.relations.SongAppKeys
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
import kotlinx.coroutines.CancellationException
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

    /**
     * @return 追加した履歴の ID。あとから再生時間を書き込むのに使う
     */
    suspend fun saveSong(playingSongData: PlayingSongData): Long {
        val mediaId = playingSongData.songData.mediaId
        val app = playingSongData.songData.app
        // 先にローカル DB を確認し、既知の曲なら API を叩かずに履歴だけ追加する
        val known = appSongKeyUseCase.getAppSongKeys(mediaId, app)
        return if (known != null) {
            saveKnownSong(known, playingSongData)
        } else {
            saveNewSong(playingSongData)
        }
    }

    /**
     * 履歴に再生時間を足す。1 曲の再生時間は一時停止や曲の切り替えのたびに分けて届く
     */
    suspend fun addListeningTime(historyId: Long, listeningMs: Long) {
        withContext(Dispatchers.IO) {
            historiesUseCase.addListeningTime(historyId, listeningMs)
        }
    }

    private suspend fun saveKnownSong(known: SongAppKeys, playingSongData: PlayingSongData): Long {
        val songId = known.targetKey.songId
        val historyId = withContext(Dispatchers.IO) {
            saveHistory(songId, playingSongData.playTime, playingSongData.songData.app)
        }
        // オフライン時などに API 情報なしで保存された曲は、URL をあとから補完する
        if (known.songKeys.song.url == null) {
            backfillSongLink(songId, playingSongData)
        }
        return historyId
    }

    private suspend fun backfillSongLink(songId: Long, playingSongData: PlayingSongData) {
        val response = fetchSongLink(playingSongData) ?: return
        if (response.status != SongLinkResponse.Status.OK) return
        val (keyMap, urlMap) = extractKeyAndUrlMap(response.songData)
        withContext(Dispatchers.IO) {
            checkAppSongKey(songId, keyMap, urlMap)
            response.songData.pageUrl.takeIf { it.isNotBlank() }?.let { pageUrl ->
                songsUseCase.updateUrl(songId, pageUrl)
            }
        }
    }

    private suspend fun saveNewSong(playingSongData: PlayingSongData): Long {
        val response = fetchSongLink(playingSongData)
        return if (response != null && response.status == SongLinkResponse.Status.OK) {
            saveWithApi(response.songData, playingSongData)
        } else {
            saveNoApi(playingSongData, response?.status ?: SongLinkResponse.Status.Error)
        }
    }

    /**
     * song.link API を呼ぶ。通信に失敗したら null を返し、呼び出し側はローカル情報だけで保存する。
     */
    private suspend fun fetchSongLink(playingSongData: PlayingSongData): SongLinkResponse? {
        return try {
            songLinkApiUseCase.getSongLinkData(
                playingSongData.songData.mediaId,
                playingSongData.songData.app,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(LOG_TAG, "song.link request failed, saving without api data", e)
            null
        }
    }

    private fun extractKeyAndUrlMap(
        songLinkData: SongLinkData,
    ): Pair<Map<MusicApp, String>, Map<MusicApp, String?>> {
        val keyMap: Map<MusicApp, String> = songLinkData.entities.values.mapNotNull { entity ->
            MusicApp.entries.firstOrNull { app -> app.apiProvider == entity.provider }
                ?.let { app -> app to entity.id }
        }.toMap()
        val urlMap: Map<MusicApp, String?> = songLinkData.links.mapNotNull { (platform, link) ->
            MusicApp.entries.firstOrNull { app -> app.platform == platform }
                ?.let { app -> app to link.url }
        }.toMap()
        return keyMap to urlMap
    }

    private suspend fun saveWithApi(
        songLinkData: SongLinkData,
        playingSongData: PlayingSongData,
    ): Long {
        val (keyMap, urlMap) = extractKeyAndUrlMap(songLinkData)
        // 問い合わせたエンティティのサムネイルを優先し、無ければ他のエンティティ、
        // それも無ければ再生中のアートワークを使う
        val thumbnailUrl = (
            songLinkData.entities[songLinkData.entityUniqueId]
                ?: songLinkData.entities.values.firstOrNull()
            )?.thumbnailUrl?.takeIf { it.isNotBlank() }
        return withContext(Dispatchers.IO) {
            saveData(
                artist = playingSongData.songData.artist,
                albumName = playingSongData.songData.album,
                thumbnail = thumbnailUrl ?: playingSongData.songData.artwork?.toBase64(),
                isThumbUrl = thumbnailUrl != null,
                title = playingSongData.songData.title,
                url = songLinkData.pageUrl.takeIf { it.isNotBlank() },
                playTime = playingSongData.playTime,
                // 再生元アプリの mediaId は必ず保存し、次回以降の同定に使えるようにする
                mapKey = keyMap + (playingSongData.songData.app to playingSongData.songData.mediaId),
                mapUrl = urlMap,
                status = SongLinkResponse.Status.OK,
                mediaId = playingSongData.songData.mediaId,
                app = playingSongData.songData.app,
            )
        }
    }

    private suspend fun saveNoApi(
        playingSongData: PlayingSongData,
        status: SongLinkResponse.Status,
    ): Long {
        val keyMap: Map<MusicApp, String> =
            mapOf(playingSongData.songData.app to playingSongData.songData.mediaId)
        return withContext(Dispatchers.IO) {
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
    ): Long {
        val artistId: Long = saveArtist(artist)
        val albumId: Long = saveAlbum(artistId, albumName, thumbnail, isThumbUrl)
        val songId = saveSong(title, artistId, albumId, url)
        val historyId = saveHistory(songId, playTime, app)
        checkAppSongKey(
            songId = songId,
            mapKey,
            mapUrl,
        )
        if (status == SongLinkResponse.Status.Error) {
            saveTasks(songId, mediaId)
        }
        return historyId
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
        isThumbUrl: Boolean,
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

    private suspend fun saveHistory(songId: Long, playTime: LocalDateTime, app: MusicApp): Long {
        return historiesUseCase.saveHistories(songId, playTime, app)
    }

    /**
     * まだ保存されていないキーだけを追加する
     */
    private suspend fun checkAppSongKey(
        songId: Long,
        keyMap: Map<MusicApp, String>,
        urlMap: Map<MusicApp, String?>,
    ) {
        val result = appSongKeyUseCase.getBySongId(songId)
        val addKeyMap = keyMap.filter { (app, mediaKey) ->
            result.none { appSongKey: AppSongKey ->
                appSongKey.app == app && appSongKey.mediaKey == mediaKey
            }
        }
        if (addKeyMap.isNotEmpty()) {
            appSongKeyUseCase.insertAll(songId, addKeyMap, urlMap)
        }
    }

    private suspend fun saveTasks(songId: Long, mediaId: String) {
        tasksUseCase.saveTasks(mediaId, songId)
    }

    companion object {
        private const val LOG_TAG = "SaveModel"
    }
}
