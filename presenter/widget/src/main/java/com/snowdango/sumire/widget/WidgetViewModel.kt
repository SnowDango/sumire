package com.snowdango.sumire.widget

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.snowdango.sumire.data.entity.playing.PlayingSongData
import com.snowdango.sumire.data.entity.preference.WidgetActionType
import com.snowdango.sumire.data.util.toBase64
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import com.snowdango.sumire.model.SettingsModel
import com.snowdango.sumire.model.ShareSongModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.URLEncoder


// aacではないが立ち位置としてViewModel
class WidgetViewModel(val context: Context) : KoinComponent {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val playingSongSharedFlow: PlayingSongSharedFlow by inject()
    private val shareSongModel: ShareSongModel by inject()
    private val settingsModel: SettingsModel by inject()
    private val widget: SmallArtworkWidget by inject()

    init {
        // 更新用
        playingSongSharedFlow.listener = {
            coroutineScope.launch {
                update(it)
            }
        }
    }

    private suspend fun update(playingSongData: PlayingSongData?) {
        val manager = GlanceAppWidgetManager(context)
        manager.getGlanceIds(SmallArtworkWidget::class.java)
            .forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { preferences ->
                    preferences[artworkKey] = playingSongData?.songData?.artwork?.toBase64() ?: ""
                    preferences[titleKey] = playingSongData?.songData?.title ?: ""
                    preferences[artist] = playingSongData?.songData?.artist ?: ""
                    preferences[mediaId] = playingSongData?.songData?.mediaId ?: ""
                    preferences[platform] = playingSongData?.songData?.app?.platform ?: ""
                }
                widget.update(context, glanceId)
            }
    }

    fun refresh() {
        coroutineScope.launch {
            val playingSongData = playingSongSharedFlow.getCurrentPlayingSong()
            update(playingSongData)
        }
    }

    fun shareSong(
        context: Context,
        title: String?,
        artist: String?,
        artwork: Bitmap?,
        mediaId: String?,
        appPlatform: String?
    ) {
        coroutineScope.launch {
            val url = shareSongModel.getUrl(mediaId, appPlatform)
            url?.let {
                val type = settingsModel.getWidgetActionType()
                when (type) {
                    WidgetActionType.COPY -> {
                        copyClipboard(context, url)
                    }

                    WidgetActionType.TWITTER -> {
                        val message = "$title - $artist\n#NowPlaying\n$url"
                        val uri = "twitter://post?message=${URLEncoder.encode(message, "utf-8")}"
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            data = Uri.parse(uri)
                        }
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    private fun copyClipboard(context: Context, copyText: String?) {
        val clipboardManager: ClipboardManager =
            context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", copyText))
    }

    companion object {
        val artworkKey = stringPreferencesKey("artwork")
        val titleKey = stringPreferencesKey("title")
        val artist = stringPreferencesKey("artist")
        val mediaId = stringPreferencesKey("mediaId")
        val platform = stringPreferencesKey("platform")
    }

}