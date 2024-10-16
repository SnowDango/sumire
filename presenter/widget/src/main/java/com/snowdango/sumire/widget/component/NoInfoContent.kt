package com.snowdango.sumire.widget.component


import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size

@Composable
fun NoInfoContent() {
    val size = LocalSize.current
    val minSize = if (size.width - size.height < (-1).dp) {
        size.width
    } else {
        size.height
    }
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            provider = ImageProvider(com.snowdango.sumire.ui.R.mipmap.ic_launcher),
            contentScale = ContentScale.Fit,
            contentDescription = null,
            modifier = GlanceModifier
                .size(minSize)
        )
    }
}