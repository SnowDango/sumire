package com.snowdango.sumire.widget.actions

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.snowdango.sumire.data.entity.preference.WidgetActionType
import com.snowdango.sumire.infla.LogEvent
import com.snowdango.sumire.model.SettingsModel
import com.snowdango.sumire.model.ShareSongModel
import com.snowdango.sumire.widget.worker.ShareSongFailureWorker
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ShareSongAction : ActionCallback, KoinComponent {

    private val shareSongModel: ShareSongModel by inject()
    private val settingsModel: SettingsModel by inject()
    private val logEvent: LogEvent by inject()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val title = parameters[titleKey]
        val artist = parameters[artistKey]
        val mediaId = parameters[mediaIdKey]
        val appPlatform = parameters[appPlatformKey]
        val url = shareSongModel.getUrl(mediaId, appPlatform)
        if (url.isNullOrBlank()) {
            logEvent.sendEvent(
                LogEvent.Event.SHARE_EVENT,
                params = mapOf(
                    LogEvent.Param.PARAM_ERROR to "url isNullOrBlank",
                    LogEvent.Param.PARAM_TITLE to title.orEmpty(),
                    LogEvent.Param.PARAM_ARTIST to artist.orEmpty(),
                    LogEvent.Param.PARAM_APP_NAME to appPlatform.orEmpty(),
                    LogEvent.Param.PARAM_MEDIA_ID to mediaId.orEmpty()
                ),
            )
            enqueueFailureWorker(context)
        } else {
            val type = settingsModel.getWidgetActionType()
            when (type) {
                WidgetActionType.COPY -> {
                    onActionCopy(context, url)
                }

                WidgetActionType.TWITTER -> {
                    onActionTwitter(context, title, artist, url)
                }
            }
        }
    }

    private fun onActionCopy(
        context: Context,
        url: String,
    ) {
        val clipboardManager: ClipboardManager =
            context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", url))
        logEvent.sendEvent(
            LogEvent.Event.SHARE_EVENT,
            params = mapOf(
                LogEvent.Param.PARAM_SHARE_TYPE to "copy",
                LogEvent.Param.PARAM_URL to url,
            ),
        )
    }

    private fun onActionTwitter(
        context: Context,
        title: String?,
        artist: String?,
        url: String,
    ) {
        if (title != null && artist != null) {
            val message = "$title - $artist\n#NowPlaying\n$url"
            val intent = Intent(Intent.ACTION_SEND)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Intent.EXTRA_TEXT, message)
                .setType("text/plain")
                .setPackage("com.twitter.android")
            logEvent.sendEvent(
                LogEvent.Event.SHARE_EVENT,
                params = mapOf(
                    LogEvent.Param.PARAM_SHARE_TYPE to "twitter",
                    LogEvent.Param.PARAM_TITLE to title,
                    LogEvent.Param.PARAM_ARTIST to artist,
                    LogEvent.Param.PARAM_URL to url,
                ),
            )
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // X がインストールされていない端末では共有先の選択に切り替える
                Log.w(LOG_TAG, "twitter app not found, fallback to chooser", e)
                val chooser = Intent.createChooser(intent.setPackage(null), null)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                runCatching { context.startActivity(chooser) }
                    .onFailure { enqueueFailureWorker(context) }
            }
        } else {
            enqueueFailureWorker(context)
            logEvent.sendEvent(
                LogEvent.Event.SHARE_EVENT,
                params = mapOf(
                    LogEvent.Param.PARAM_SHARE_TYPE to "twitter",
                    LogEvent.Param.PARAM_ERROR to "not found metadata",
                ),
            )
        }
    }

    private fun enqueueFailureWorker(context: Context) {
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<ShareSongFailureWorker>().build())
    }

    companion object {
        private const val LOG_TAG = "ShareSongAction"
        val titleKey = ActionParameters.Key<String>("title")
        val artistKey = ActionParameters.Key<String>("artist")
        val mediaIdKey = ActionParameters.Key<String>("mediaId")
        val appPlatformKey = ActionParameters.Key<String>("appPlatform")
    }
}
