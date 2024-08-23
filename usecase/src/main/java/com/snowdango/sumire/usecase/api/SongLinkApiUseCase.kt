package com.snowdango.sumire.usecase.api

import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.entity.songlink.SongLinkResponse
import com.snowdango.sumire.repository.SongLinkApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SongLinkApiUseCase : KoinComponent {

    private val api: SongLinkApi by inject()

    suspend fun getSongLinkData(id: String, app: MusicApp): SongLinkResponse {
        val appString = app.platform
        return api.getSongData(id, appString)
    }


}