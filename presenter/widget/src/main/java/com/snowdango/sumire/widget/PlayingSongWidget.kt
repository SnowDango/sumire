package com.snowdango.sumire.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize

class PlayingSongWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode
        get() = SizeMode.Responsive(
            sizes = setOf(SMALL_SQUARE, HORIZONTAL_RECTANGLE)
        )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.d("WidgetSize", sizeMode.toString())
        provideContent {
            Content()
        }
    }

    @Composable
    fun Content() {
        Box(
            modifier = GlanceModifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(com.snowdango.sumire.ui.R.mipmap.ic_launcher_round),
                contentDescription = null,
                modifier = GlanceModifier
                    .clickable {
                        
                    }
            )
        }
    }

    companion object {
        private val SMALL_SQUARE = DpSize(100.dp, 100.dp)
        private val HORIZONTAL_RECTANGLE = DpSize(250.dp, 100.dp)
    }

}