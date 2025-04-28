package com.snowdango.sumire.infla

import android.content.Context
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

class LogEvent(
    private val context: Context,
) {

    private val analytics = FirebaseAnalytics.getInstance(context)

    fun sendEvent(event: Event, params: Map<Param, String>) {
        analytics.logEvent(event.eventName) {
            params.forEach {
                param(it.key.paramName, it.value)
            }
        }
        Log.d(
            "LogEvent",
            "event: ${event.eventName} \n" +
                params.map { param -> "${param.key.paramName}: ${param.value}" }
                    .joinToString(separator = "\n"),
        )
    }

    companion object {
        // analytics
        const val SHARE_EVENT = "share"

        // screen
        const val VIEW_SCREEN_EVENT = "view_screen"

        // save
        const val SAVE_HISTORY_EVENT = "save_song_data"

        // failure
        const val FAILED_EVENT = "failed"
    }

    enum class Param(val paramName: String) {
        PARAM_ERROR("error"),
        PARAM_SCREEN("screen"),
        PARAM_SHARE_TYPE("share_type"),
        PARAM_TITLE("title"),
        PARAM_ALBUM("album"),
        PARAM_ARTIST("artist"),
        PARAM_URL("url"),
        PARAM_APP_NAME("app_name"),
        PARAM_MEDIA_ID("media_id"),
    }

    enum class Event(val eventName: String) {
        SHARE_EVENT(LogEvent.SHARE_EVENT),
        VIEW_SCREEN_EVENT(LogEvent.VIEW_SCREEN_EVENT),
        SAVE_HISTORY_EVENT(LogEvent.SAVE_HISTORY_EVENT),
        FAILED_EVENT(LogEvent.FAILED_EVENT),
    }
}
