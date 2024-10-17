package com.snowdango.sumire.receiver

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.snowdango.sumire.widget.SmallArtworkWidget
import com.snowdango.sumire.widget.worker.SmallArtworkWidgetWorker
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class SmallArtworkWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {
    
    private val widget: SmallArtworkWidget by inject()
    private val WORKER_TAG = this::class.java.name

    override val glanceAppWidget: GlanceAppWidget
        get() = widget

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<SmallArtworkWidgetWorker>().build())
    }

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
        WorkManager.getInstance(context)
            .cancelAllWorkByTag(WORKER_TAG)
    }

    @SuppressLint("InvalidPeriodicWorkRequestInterval")
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val request = PeriodicWorkRequest.Builder(
            SmallArtworkWidgetWorker::class.java,
            30,
            TimeUnit.SECONDS,
        ).addTag(
            WORKER_TAG,
        ).build()
        WorkManager.getInstance(context)
            .enqueue(request)
    }
}
