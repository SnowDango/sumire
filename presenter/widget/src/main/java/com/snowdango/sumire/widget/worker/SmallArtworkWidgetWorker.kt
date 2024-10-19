package com.snowdango.sumire.widget.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.snowdango.sumire.widget.SmallArtworkWidget
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class SmallArtworkWidgetWorker(
    context: Context,
    workerParameters: WorkerParameters
) : PlayingSongWorker<SmallArtworkWidget>(
    context,
    workerParameters
), KoinComponent {

    override val widget: SmallArtworkWidget by inject { parametersOf(context) }

}