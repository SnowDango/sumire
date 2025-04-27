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

    private val settingModel: SettingsModel by inject()

    val settingsFlow = settingModel.getSettingsFlow().shareIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
    )

    fun setWidgetActionType(widgetActionType: WidgetActionType) {
        viewModelScope.launch(Dispatchers.IO) {
            settingModel.setWidgetActionType(widgetActionType)
        }
    }

    fun setUrlPlatform(urlPlatform: String) {
        viewModelScope.launch(Dispatchers.IO) {
            settingModel.setUrlPlatform(urlPlatform)
        }
    }
}
