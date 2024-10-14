package com.snowdango.sumire.data.entity

import com.snowdango.sumire.data.entity.preference.WidgetActionType

data class SettingsPreferences(
    val widgetActionType: WidgetActionType,
    val urlPlatform: String,
)
