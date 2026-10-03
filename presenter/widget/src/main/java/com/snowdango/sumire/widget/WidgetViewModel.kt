package com.snowdango.sumire.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.snowdango.sumire.data.entity.playing.PlayingSongData
import com.snowdango.sumire.data.util.toBase64
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// aacではないが立ち位置としてViewModel
class WidgetViewModel(val context: Context) : KoinComponent {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val playingSongSharedFlow: PlayingSongSharedFlow by inject()
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
        // Base64 化は重いので glanceId ごとではなく一度だけ行う
        val artwork = playingSongData?.songData?.artwork?.toBase64() ?: ""
        manager.getGlanceIds(SmallArtworkWidget::class.java)
            .forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { preferences ->
                    preferences[SmallArtworkWidget.artworkKey] = artwork
                    preferences[SmallArtworkWidget.titleKey] = playingSongData?.songData?.title ?: ""
                    preferences[SmallArtworkWidget.artistKey] = playingSongData?.songData?.artist ?: ""
                    preferences[SmallArtworkWidget.mediaIdKey] = playingSongData?.songData?.mediaId ?: ""
                    preferences[SmallArtworkWidget.platformKey] = playingSongData?.songData?.app?.platform ?: ""
                    preferences[SmallArtworkWidget.isSharedFailureKey] = false
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
}
