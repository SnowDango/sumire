package com.snowdango.sumire.widget

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.snowdango.sumire.data.entity.playing.PlayingSongData
import com.snowdango.sumire.data.util.toBase64
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import com.snowdango.sumire.model.ShareSongModel
import com.snowdango.sumire.widget.worker.PlayingSongWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


// aacではないが立ち位置としてViewModel
class WidgetViewModel(val context: Context) : KoinComponent {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val playingSongSharedFlow: PlayingSongSharedFlow by inject()
    private val shareSongModel: ShareSongModel by inject()
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
                    preferences[PlayingSongWorker.artworkKey] =
                        playingSongData?.songData?.artwork?.toBase64() ?: ""
                    preferences[PlayingSongWorker.titleKey] =
                        playingSongData?.songData?.title ?: ""
                    preferences[PlayingSongWorker.mediaId] =
                        playingSongData?.songData?.mediaId ?: ""
                    preferences[PlayingSongWorker.platform] =
                        playingSongData?.songData?.app?.platform ?: ""
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

    fun copyUrl(context: Context, mediaId: String?, appPlatform: String?) {
        coroutineScope.launch {
            val url = shareSongModel.getUrl(mediaId, appPlatform)
            url?.let {
                copyClipboard(context, it)
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
        val mediaId = stringPreferencesKey("mediaId")
        val platform = stringPreferencesKey("platform")
    }

}