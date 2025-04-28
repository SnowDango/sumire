package com.snowdango.sumire.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

class AppWidgetSizeProvider(
    private val context: Context,
) {

    private val appWidgetManager = AppWidgetManager.getInstance(context)

    fun getWidgetSizePx(widgetId: Int): Pair<Int, Int> {
        val dpSize = getWidgetsSizeDp(widgetId)
        return context.dip(dpSize.width.value) to context.dip(dpSize.height.value)
    }

    fun getWidgetsSizeDp(widgetId: Int): DpSize {
        val width = getWidgetWidth(widgetId)
        val height = getWidgetHeight(widgetId)
        return DpSize(width.dp, height.dp)
    }

    private fun getWidgetWidth(widgetId: Int): Int =
        getWidgetSizeInDp(widgetId, AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)

    private fun getWidgetHeight(widgetId: Int): Int =
        getWidgetSizeInDp(widgetId, AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)

    private fun getWidgetSizeInDp(widgetId: Int, key: String): Int =
        appWidgetManager.getAppWidgetOptions(widgetId).getInt(key, 0)

    private fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()
}
