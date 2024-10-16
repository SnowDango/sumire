package com.snowdango.sumire.ui.theme.glance

import androidx.compose.ui.graphics.Color
import androidx.glance.color.ColorProvider
import androidx.glance.color.colorProviders


val glanceColors = colorProviders(
    primary = ColorProvider(Color(188, 175, 219), Color(159, 202, 252)),
    onPrimary = ColorProvider(Color.White, Color(0, 50, 86)),
    primaryContainer = ColorProvider(Color(208, 228, 255), Color(22, 73, 116)),
    onPrimaryContainer = ColorProvider(Color(0, 29, 53), Color(208, 228, 255)),
    secondary = ColorProvider(Color(82, 95, 112), Color(186, 200, 219)),
    onSecondary = ColorProvider(Color.White, Color(36, 49, 64)),
    secondaryContainer = ColorProvider(Color(214, 228, 247), Color(59, 72, 87)),
    onSecondaryContainer = ColorProvider(Color(15, 29, 42), Color(214, 228, 247)),
    tertiary = ColorProvider(Color(106, 87, 121), Color(213, 190, 229)),
    onTertiary = ColorProvider(Color.White, Color(58, 41, 72)),
    tertiaryContainer = ColorProvider(Color(241, 218, 255), Color(81, 64, 96)),
    onTertiaryContainer = ColorProvider(Color(36, 20, 50), Color(241, 218, 255)),
    error = ColorProvider(Color(186, 26, 26), Color(255, 180, 171)),
    onError = ColorProvider(Color.White, Color(105, 0, 5)),
    errorContainer = ColorProvider(Color(255, 218, 214), Color(147, 0, 10)),
    onErrorContainer = ColorProvider(Color(65, 0, 2), Color(255, 218, 214)),
    background = ColorProvider(Color(248, 249, 255), Color(16, 20, 24)),
    onBackground = ColorProvider(Color(25, 28, 32), Color(225, 226, 232)),
    surface = ColorProvider(Color(248, 249, 255), Color(16, 20, 24)),
    onSurface = ColorProvider(Color(25, 28, 32), Color(225, 226, 232)),
    surfaceVariant = ColorProvider(Color(223, 227, 235), Color(66, 71, 78)),
    onSurfaceVariant = ColorProvider(Color(66, 71, 78), Color(194, 188, 207)),
    outline = ColorProvider(Color(115, 119, 127), Color(140, 145, 153)),
    inverseSurface = ColorProvider(Color(45, 49, 53), Color(225, 226, 232)),
    inverseOnSurface = ColorProvider(Color(239, 240, 247), Color(45, 49, 53)),
    inversePrimary = ColorProvider(Color(159, 202, 252), Color(52, 97, 141)),
    widgetBackground = ColorProvider(Color(188, 175, 219), Color(159, 202, 252)),
)