package com.snowdango.sumire.widget.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.snowdango.sumire.widget.SmallArtworkWidget

class SmallArtworkWidgetWorker(
    context: Context,
    workerParameters: WorkerParameters
): PlayingSongWorker<SmallArtworkWidget>(
    context,
    workerParameters
) {

    override val widget: SmallArtworkWidget
        get() = SmallArtworkWidget()

}