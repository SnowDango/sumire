package com.snowdango.sumire.repository

import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.entity.songlink.SongLinkData
import com.snowdango.sumire.data.entity.songlink.SongLinkResponse
import com.snowdango.sumire.repository.ktor.KtorClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode

class SongLinkApi {

    private val client: HttpClient by lazy {
        KtorClient.getClient()
    }

    suspend fun getSongData(id: String, app: MusicApp): SongLinkResponse {
        val response = client.get {
            url("/v1-alpha.1/links")
            parameter("platform", "appleMusic")
            parameter("type", "song")
            parameter("id", id)
            parameter("userCountry", "JP")
        }
        return SongLinkResponse(
            status = when (response.status) {
                HttpStatusCode.OK -> SongLinkResponse.Status.OK
                HttpStatusCode.BadRequest -> SongLinkResponse.Status.NOT_FOUND
                else -> SongLinkResponse.Status.Error
            },
            songData = response.body<SongLinkData>()
        )

    }
}