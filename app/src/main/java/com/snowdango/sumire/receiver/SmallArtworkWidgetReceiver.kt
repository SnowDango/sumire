package com.snowdango.sumire.receiver

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.snowdango.sumire.widget.SmallArtworkWidget
import com.snowdango.sumire.widget.worker.SmallArtworkWidgetWorker
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class SmallArtworkWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {

    private val widget: SmallArtworkWidget by inject()
    private val workerTag = this::class.java.name

    override val glanceAppWidget: GlanceAppWidget
        get() = widget

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<SmallArtworkWidgetWorker>().build())
    }

    override fun onRestored(context: Context, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<SmallArtworkWidgetWorker>().build())
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WorkManager.getInstance(context).also {
            it.cancelUniqueWork(workerTag)
            it.cancelAllWorkByTag(workerTag)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // WorkManager の周期は 15 分未満にできない(指定しても 15 分に丸められる)。
        // 重複登録しないように unique work として登録する。
        val request = PeriodicWorkRequestBuilder<SmallArtworkWidgetWorker>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
        ).addTag(
            workerTag,
        ).build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(workerTag, ExistingPeriodicWorkPolicy.KEEP, request)
    }
}
