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
        val app = MusicApp.entries.firstOrNull { it.platform == appPlatform }
        if (mediaId.isNullOrBlank() || app == null) {
            return null
        }
        val urlMap = getUrlMap(mediaId, app)
        return if (urlMap.isNullOrEmpty()) {
            null
        } else {
            Log.d("ShareSongModel", "getUrlMap: $urlMap")
            val priorityPlatform = settingsUseCase.getUrlPlatform()
            urlMap[priorityPlatform.platform]
        }
    }

    private suspend fun getUrlMap(mediaId: String?, app: MusicApp): Map<String, String>? {
        if (mediaId == null) return null
        val keys = appSongKeyUseCase.getAppSongKeys(mediaId, app)
        Log.d("ShareSongModel", "getUrlMap: $keys")
        val urlMap: MutableMap<String, String> = mutableMapOf()
        keys?.songKeys?.appSongKeys?.forEach { key ->
            key.url?.let {
                urlMap[key.app.platform] = it
            }
        }
        keys?.songKeys?.song?.url?.let {
            urlMap["songlink"] = it
        }
        return urlMap
    }
}
