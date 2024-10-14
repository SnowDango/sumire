package com.snowdango.sumire.model

import android.util.Log
import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.usecase.db.AppSongKeyUseCase
import com.snowdango.sumire.usecase.setting.SettingsUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ShareSongModel : KoinComponent {

    private val settingsUseCase: SettingsUseCase by inject()
    private val appSongKeyUseCase: AppSongKeyUseCase by inject()

    suspend fun getUrl(mediaId: String?, appPlatform: String?): String? {
        if (mediaId.isNullOrBlank()) {
            return null
        }
        val app = MusicApp.entries.firstOrNull { it.platform == appPlatform } ?: return null
        val urlMap = getUrlMap(mediaId, app) ?: return null
        val priorityPlatform = settingsUseCase.getUrlPlatform()
        urlMap[priorityPlatform.platform]?.let {
            return it // 優先されたplatformのurlを取得
        }
        urlMap[app.platform]?.let {
            return it
        }
        return null
    }

    private suspend fun getUrlMap(mediaId: String?, app: MusicApp): Map<String, String>? {
        if (mediaId == null) return null
        val keys = appSongKeyUseCase.getAppSongKeys(mediaId, app)
        val urlMap: MutableMap<String, String> = mutableMapOf()
        keys.appSongKeys.forEach { key ->
            key.url?.let {
                urlMap[key.app.platform] = it
            }
        }
        keys.song.url?.let {
            urlMap["songlink"] = it
        }
        return urlMap
    }

}