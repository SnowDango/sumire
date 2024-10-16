package com.snowdango.sumire.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import com.snowdango.sumire.data.util.toBitmap
import com.snowdango.sumire.ui.theme.glance.SumireGlanceTheme
import com.snowdango.sumire.widget.component.NoInfoContent
import com.snowdango.sumire.widget.component.SmallArtworkContent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SmallArtworkWidget() : GlanceAppWidget(), KoinComponent {

    private val widgetViewModel: WidgetViewModel by inject<WidgetViewModel>()
    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    init {
        widgetViewModel.refresh()
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val current = currentState<Preferences>()
            val title = current[WidgetViewModel.titleKey]
            val artist = current[WidgetViewModel.artist]
            val artwork = current[WidgetViewModel.artworkKey]
            val mediaId = current[WidgetViewModel.mediaId]
            val platform = current[WidgetViewModel.platform]
            SumireGlanceTheme {
                Content(
                    title,
                    artwork,
                    onClick = {
                        widgetViewModel.shareSong(
                            context,
                            title,
                            artist,
                            artwork?.toBitmap(),
                            mediaId,
                            platform
                        )
                    }
                )
            }
        }
    }

    @Composable
    fun Content(
        title: String?,
        artwork: String?,
        onClick: () -> Unit
    ) {
        if (title != null) {
            SmallArtworkContent(title, artwork) {
                onClick.invoke()
            }
        } else {
            NoInfoContent()
        }
    }
}