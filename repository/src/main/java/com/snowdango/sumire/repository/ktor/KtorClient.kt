package com.snowdango.sumire.repository.ktor

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.serialization.kotlinx.json.json


object KtorClient {
    fun getClient(): HttpClient {
        return HttpClient(Android) {
            install(DefaultRequest) {
                url("https://api.song.link/v1-alpha.1")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(DefaultJson)
            }
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.INFO
            }
            expectSuccess = false
        }
    }
}