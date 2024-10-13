package com.snowdango.sumire.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.action
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.snowdango.sumire.data.util.toBitmap
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SmallArtworkWidget : GlanceAppWidget(), KoinComponent {

    private val widgetViewModel: WidgetViewModel by inject()
    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    init {
        widgetViewModel.refresh()
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val current = currentState<Preferences>()
            val title = current[WidgetViewModel.titleKey]
            val artwork = current[WidgetViewModel.artworkKey]
            val mediaId = current[WidgetViewModel.mediaId]
            val platform = current[WidgetViewModel.platform]
            Content(
                title,
                artwork,
                mediaId,
                platform,
                onClick = { id, app ->
                    widgetViewModel.copyUrl(context, id, app)
                }
            )
        }
    }

    @Composable
    fun Content(
        title: String?,
        artwork: String?,
        mediaId: String?,
        appPlatform: String?,
        onClick: (id: String?, appPlatform: String?) -> Unit
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable(action {
                    onClick.invoke(
                        mediaId,
                        appPlatform
                    )
                }),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val size = LocalSize.current
            val minSize = if (size.width - size.height < (-1).dp) {
                size.width
            } else {
                size.height
            }
            Image(
                provider = artwork?.toBitmap()?.let {
                    ImageProvider(it.getRoundedCornerBitmap(LocalContext.current, 12.dp))
                } ?: ImageProvider(com.snowdango.sumire.ui.R.mipmap.ic_launcher),
                contentScale = ContentScale.Fit,
                contentDescription = null,
                modifier = GlanceModifier
                    .size(minSize * 4 / 5)
                    .cornerRadius(12.dp)
            )
            Text(
                text = title ?: "",
                style = TextStyle(
                    color = ColorProvider(Color.White, Color.White),
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1,
                modifier = GlanceModifier
                    .padding(top = 8.dp)
            )
        }
    }

}