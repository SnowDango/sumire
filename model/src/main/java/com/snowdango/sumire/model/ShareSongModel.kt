package com.snowdango.sumire.model

import android.util.Log
import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.usecase.db.AppSongKeyUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ShareSongModel : KoinComponent {

    private val appSongKeyUseCase: AppSongKeyUseCase by inject()

    suspend fun getUrl(mediaId: String?, appPlatform: String?): String? {
        if (mediaId.isNullOrBlank()) {
            return null
        }
        val app = MusicApp.entries.firstOrNull { it.platform == appPlatform }
        if (app == null) {
            return null
        }
        Log.d("url get database", "mediaId: $mediaId \nplatform: ${app.platform}")
        return appSongKeyUseCase.getUrlByKey(mediaId, app)
    }

}