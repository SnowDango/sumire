package com.snowdango.sumire.model

import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.entity.preference.UrlPriorityPlatform
import com.snowdango.sumire.usecase.db.AppSongKeyUseCase
import com.snowdango.sumire.usecase.setting.SettingsUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ShareSongModel : KoinComponent {

    private val settingsUseCase: SettingsUseCase by inject()
    private val appSongKeyUseCase: AppSongKeyUseCase by inject()

    suspend fun getUrl(mediaId: String?, appPlatform: String?): String? {
        val app = MusicApp.entries.firstOrNull { it.platform == appPlatform }
        if (mediaId.isNullOrBlank() || app == null) {
            return null
        }
        val urlMap = getUrlMap(mediaId, app)
        if (urlMap.isEmpty()) {
            return null
        }
        val priorityPlatform = settingsUseCase.getUrlPlatform()
        // 優先サービスの URL が無ければ song.link のページ、それも無ければ持っている URL のどれか
        return urlMap[priorityPlatform.platform]
            ?: urlMap[UrlPriorityPlatform.SONG_LINK.platform]
            ?: urlMap.values.firstOrNull()
    }

    private suspend fun getUrlMap(mediaId: String, app: MusicApp): Map<String, String> {
        val keys = appSongKeyUseCase.getAppSongKeys(mediaId, app) ?: return emptyMap()
        val urlMap: MutableMap<String, String> = mutableMapOf()
        keys.songKeys.appSongKeys?.forEach { key ->
            key.url?.let {
                urlMap[key.app.platform] = it
            }
        }
        keys.songKeys.song.url?.let {
            urlMap[UrlPriorityPlatform.SONG_LINK.platform] = it
        }
        return urlMap
    }
}
