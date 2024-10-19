package com.snowdango.sumire.widget.worker

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
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
    workerParameters: WorkerParameters
) : CoroutineWorker(
    context,
    workerParameters
), KoinComponent {

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
        GlanceAppWidgetManager(context)
            .getGlanceIds(SmallArtworkWidget::class.java)
            .forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { preferences ->
                    preferences[artworkKey] = playingSong?.songData?.artwork?.toBase64() ?: ""
                    preferences[titleKey] = playingSong?.songData?.title ?: ""
                    preferences[mediaId] = playingSong?.songData?.mediaId ?: ""
                    preferences[platform] = playingSong?.songData?.app?.platform ?: ""
                }
                widget.update(context, glanceId)
            }
    }

    companion object {
        val artworkKey = stringPreferencesKey("artwork")
        val titleKey = stringPreferencesKey("title")
        val mediaId = stringPreferencesKey("mediaId")
        val platform = stringPreferencesKey("platform")
    }

}