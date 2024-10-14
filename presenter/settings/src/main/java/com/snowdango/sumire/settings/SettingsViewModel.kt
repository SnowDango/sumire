package com.snowdango.sumire.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowdango.sumire.data.entity.preference.WidgetActionType
import com.snowdango.sumire.model.SettingsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel : ViewModel(), KoinComponent {

    private val preferences: SettingsModel by inject()
    val settingsFlow = preferences.settingsFlow().shareIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
    )

    fun setWidgetActionType(widgetActionType: WidgetActionType) {
        viewModelScope.launch(Dispatchers.IO) {
            preferences.editWidgetActionType(widgetActionType)
        }
    }

}