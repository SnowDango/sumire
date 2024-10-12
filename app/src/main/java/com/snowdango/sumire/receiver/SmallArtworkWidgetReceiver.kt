package com.snowdango.sumire.receiver

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import com.snowdango.sumire.widget.SmallArtworkWidget
import com.snowdango.sumire.widget.worker.SmallArtworkWidgetWorker
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SmallArtworkWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {

    private val playingSongSharedFlow: PlayingSongSharedFlow by inject()

    override val glanceAppWidget: GlanceAppWidget
        get() = SmallArtworkWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
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

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.d(SmallArtworkWidgetReceiver::class.java.name, "enable")
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<SmallArtworkWidgetWorker>().build())
        playingSongSharedFlow.listeners[SmallArtworkWidgetReceiver::class.java.name] =
            object : PlayingSongSharedFlow.ChangeListener {
                override fun onChanged() {
                    Log.d("ChangeListener", "onChange")
                    WorkManager.getInstance(context)
                        .enqueue(OneTimeWorkRequestBuilder<SmallArtworkWidgetWorker>().build())
                }
            }
    }

}
