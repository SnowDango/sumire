package com.snowdango.sumire.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.materialkolor.rememberDynamicColorScheme
import com.snowdango.sumire.ui.R

@Composable
fun SumireTheme(
    seedColor: Color = colorResource(R.color.seed),
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme =
        rememberDynamicColorScheme(seedColor = seedColor, isDark = darkTheme, isAmoled = false)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}