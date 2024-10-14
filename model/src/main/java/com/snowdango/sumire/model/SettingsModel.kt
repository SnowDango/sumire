package com.snowdango.sumire.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.snowdango.sumire.data.entity.SettingsPreferences
import com.snowdango.sumire.data.entity.preference.WidgetActionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsModel(private val dataStore: DataStore<Preferences>) {

    private val widgetActionTypeKey = stringPreferencesKey("widget_action_type")

    fun settingsFlow() = dataStore.data.map { preference ->
        SettingsPreferences(
            widgetActionType = WidgetActionType.entries.firstOrNull { it.name == preference[widgetActionTypeKey] }
                ?: WidgetActionType.COPY,
        )
    }

    suspend fun editWidgetActionType(widgetActionType: WidgetActionType) {
        dataStore.edit { preferences ->
            preferences[widgetActionTypeKey] = widgetActionType.name
        }
    }

    suspend fun getWidgetActionType(): WidgetActionType {
        return WidgetActionType.entries.firstOrNull {
            it.name == dataStore.data.first()[widgetActionTypeKey]
        } ?: WidgetActionType.COPY
    }

}