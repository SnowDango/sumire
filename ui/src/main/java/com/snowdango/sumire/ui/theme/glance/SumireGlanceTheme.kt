package com.snowdango.sumire.ui.theme.glance

import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme

@Composable
fun SumireGlanceTheme(
    content: @Composable () -> Unit,
) {
    GlanceTheme(
        colors = glanceColors,
        content = content,
    )
}
