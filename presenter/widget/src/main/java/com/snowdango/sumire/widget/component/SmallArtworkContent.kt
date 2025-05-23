package com.snowdango.sumire.widget.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.snowdango.sumire.data.util.toBitmap
import com.snowdango.sumire.presenter.widget.R
import com.snowdango.sumire.widget.getRoundedCornerBitmap

@Suppress("MagicNumber")
@Composable
fun SmallArtworkContent(
    title: String,
    artwork: String,
    isSharedFailure: Boolean,
    onClick: Action,
    modifier: GlanceModifier = GlanceModifier,
) {
    val size = LocalSize.current
    val minSize = if (size.width - size.height < (-1).dp) {
        size.width
    } else {
        size.height
    }
    val artworkBitmap = artwork.toBitmap()
    Box(
        modifier = modifier
            .fillMaxSize()
            .cornerRadius(12.dp)
            .clickable(onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = GlanceModifier
                    .size(minSize * 4 / 5)
                    .cornerRadius(12.dp)
            ) {
                if (!isSharedFailure) {
                    Image(
                        provider = artworkBitmap?.let {
                            ImageProvider(it.getRoundedCornerBitmap(LocalContext.current, 12.dp))
                        } ?: ImageProvider(com.snowdango.sumire.ui.R.mipmap.ic_launcher_foreground),
                        contentScale = ContentScale.Fit,
                        contentDescription = null,
                        modifier = GlanceModifier
                            .fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .background(GlanceTheme.colors.error),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.error),
                            contentScale = ContentScale.Fit,
                            contentDescription = null,
                            modifier = GlanceModifier
                                .size(minSize * 2 / 5),
                        )
                    }
                }
            }
            Text(
                text = title,
                style = TextStyle(
                    color = ColorProvider(Color.White, Color.White),
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1,
                modifier = GlanceModifier
                    .padding(top = 8.dp),
            )
        }
    }
}
