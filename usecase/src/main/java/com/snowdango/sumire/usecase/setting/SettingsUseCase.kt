package com.snowdango.sumire.usecase.setting

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.snowdango.sumire.data.entity.SettingsPreferences
import com.snowdango.sumire.data.entity.preference.UrlPriorityPlatform
import com.snowdango.sumire.data.entity.preference.WidgetActionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsUseCase(private val dataStore: DataStore<Preferences>) {

    private val widgetActionTypeKey = stringPreferencesKey("widget_action_type")
    private val urlPlatformKey = stringPreferencesKey("url_platform")

    fun settingsFlow() = dataStore.data.map { preference ->
        SettingsPreferences(
            widgetActionType = WidgetActionType.entries.firstOrNull { it.name == preference[widgetActionTypeKey] }
                ?: WidgetActionType.COPY,
            urlPlatform = UrlPriorityPlatform.entries.firstOrNull { it.platform == preference[urlPlatformKey] }
                ?: UrlPriorityPlatform.SONG_LINK,
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

    suspend fun editUrlPlatform(urlPlatform: String) {
        dataStore.edit { preferences ->
            preferences[urlPlatformKey] = urlPlatform
        }
    }

    suspend fun getUrlPlatform(): UrlPriorityPlatform {
        return UrlPriorityPlatform.entries.firstOrNull {
            it.platform == dataStore.data.first()[urlPlatformKey]
        } ?: UrlPriorityPlatform.SONG_LINK
    }

}