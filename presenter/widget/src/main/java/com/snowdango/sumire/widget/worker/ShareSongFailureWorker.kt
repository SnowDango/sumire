package com.snowdango.sumire.widget.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.snowdango.sumire.widget.SmallArtworkWidget
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class ShareSongFailureWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(
    context,
    workerParameters,
),
    KoinComponent {

    val widget: SmallArtworkWidget by inject { parametersOf(context) }

    @Suppress("MagicNumber")
    override suspend fun doWork(): Result {
        Log.d(ShareSongFailureWorker::class.java.name, "doWork")
        update(isFailure = true)
        delay(3_000)
        update(isFailure = false)
        return Result.success()
    }

    private suspend fun update(isFailure: Boolean) {
        GlanceAppWidgetManager(context)
            .getGlanceIds(SmallArtworkWidget::class.java)
            .forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { preferences ->
                    preferences[SmallArtworkWidget.isSharedFailureKey] = isFailure
                }
                widget.update(context, glanceId)
            }
    }
}
