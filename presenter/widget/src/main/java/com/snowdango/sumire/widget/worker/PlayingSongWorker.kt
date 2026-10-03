package com.snowdango.sumire.widget.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.snowdango.sumire.data.entity.playing.PlayingSongData
import com.snowdango.sumire.data.util.toBase64
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import com.snowdango.sumire.widget.SmallArtworkWidget
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class PlayingSongWorker<T : GlanceAppWidget>(
    private val context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(
    context,
    workerParameters,
),
    KoinComponent {

    abstract val widget: T

    private val playingSongSharedFlow: PlayingSongSharedFlow by inject()

    override suspend fun doWork(): Result {
        Log.d(PlayingSongWorker::class.java.name, "doWork")
        val playingSong = playingSongSharedFlow.getCurrentPlayingSong()
        update(playingSong)
        return Result.success()
    }

    private suspend fun update(playingSong: PlayingSongData?) {
        Log.d(PlayingSongWorker::class.java.name, playingSong?.songData?.title ?: "")
        // Base64 化は重いので glanceId ごとではなく一度だけ行う
        val artwork = playingSong?.songData?.artwork?.toBase64() ?: ""
        GlanceAppWidgetManager(context)
            .getGlanceIds(SmallArtworkWidget::class.java)
            .forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { preferences ->
                    preferences[SmallArtworkWidget.artworkKey] = artwork
                    preferences[SmallArtworkWidget.titleKey] = playingSong?.songData?.title ?: ""
                    preferences[SmallArtworkWidget.artistKey] = playingSong?.songData?.artist ?: ""
                    preferences[SmallArtworkWidget.mediaIdKey] = playingSong?.songData?.mediaId ?: ""
                    preferences[SmallArtworkWidget.platformKey] =
                        playingSong?.songData?.app?.platform ?: ""
                    preferences[SmallArtworkWidget.isSharedFailureKey] = false
                }
                widget.update(context, glanceId)
            }
    }
}
