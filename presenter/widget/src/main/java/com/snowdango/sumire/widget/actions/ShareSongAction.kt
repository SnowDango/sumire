package com.snowdango.sumire.widget.actions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionStartActivity
import com.snowdango.sumire.data.entity.preference.WidgetActionType
import com.snowdango.sumire.infla.LogEvent
import com.snowdango.sumire.model.SettingsModel
import com.snowdango.sumire.model.ShareSongModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.URLEncoder

class ShareSongAction : ActionCallback, KoinComponent {

    private val shareSongModel: ShareSongModel by inject()
    private val settingsModel: SettingsModel by inject()
    private val logEvent: LogEvent by inject()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val title = parameters[titleKey]
        val artist = parameters[artistKey]
        val mediaId = parameters[mediaIdKey]
        val appPlatform = parameters[appPlatformKey]
        val url = shareSongModel.getUrl(mediaId, appPlatform)
        Log.d("ShareSongAction", url.toString())
        if (url.isNullOrBlank()) {
            Handler(context.mainLooper).post {
                Toast.makeText(context, "URLの取得に失敗しました。", Toast.LENGTH_SHORT).show()
            }
            logEvent.sendEvent(
                LogEvent.Event.SHARE_EVENT,
                params = mapOf(LogEvent.Param.PARAM_ERROR to "url isNullOrBlank")
            )
        } else {
            val type = settingsModel.getWidgetActionType()
            Log.d("ShareSongAction", type.name)
            when (type) {
                WidgetActionType.COPY -> {
                    val clipboardManager: ClipboardManager =
                        context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    clipboardManager.setPrimaryClip(ClipData.newPlainText("", url))
                    logEvent.sendEvent(
                        LogEvent.Event.SHARE_EVENT,
                        params = mapOf(
                            LogEvent.Param.PARAM_SHARE_TYPE to "copy",
                            LogEvent.Param.PARAM_URL to url,
                        )
                    )
                }

                WidgetActionType.TWITTER -> {
                    if (title != null && artist != null) {
                        val message = "$title - $artist\n#NowPlaying\n$url"
                        val uri = "twitter://post?message=${URLEncoder.encode(message, "utf-8")}"
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            data = Uri.parse(uri)
                        }
                        logEvent.sendEvent(
                            LogEvent.Event.SHARE_EVENT,
                            params = mapOf(
                                LogEvent.Param.PARAM_SHARE_TYPE to "twitter",
                                LogEvent.Param.PARAM_TITLE to title,
                                LogEvent.Param.PARAM_ARTIST to artist,
                                LogEvent.Param.PARAM_URL to url,
                            )
                        )
                        actionStartActivity(intent)
                    } else {
                        Handler(context.mainLooper).post {
                            Toast.makeText(
                                context,
                                "metadataの取得に失敗しました",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        logEvent.sendEvent(
                            LogEvent.Event.SHARE_EVENT,
                            params = mapOf(
                                LogEvent.Param.PARAM_SHARE_TYPE to "twitter",
                                LogEvent.Param.PARAM_ERROR to "not found metadata",
                            )
                        )
                    }
                }
            }
        }
    }

    companion object {
        val titleKey = ActionParameters.Key<String>("title")
        val artistKey = ActionParameters.Key<String>("artist")
        val mediaIdKey = ActionParameters.Key<String>("mediaId")
        val appPlatformKey = ActionParameters.Key<String>("appPlatform")
    }
}