package com.snowdango.sumire.model

import com.snowdango.sumire.data.entity.preference.UrlPriorityPlatform
import com.snowdango.sumire.data.entity.preference.WidgetActionType
import com.snowdango.sumire.usecase.setting.SettingsUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsModel : KoinComponent {

    private val settingsUseCase: SettingsUseCase by inject()

    fun getSettingsFlow() = settingsUseCase.settingsFlow()

    suspend fun setWidgetActionType(widgetActionType: WidgetActionType) {
        settingsUseCase.editWidgetActionType(widgetActionType)
    }

    suspend fun getWidgetActionType(): WidgetActionType {
        return settingsUseCase.getWidgetActionType()
    }

    suspend fun setUrlPlatform(urlPlatform: String) {
        settingsUseCase.editUrlPlatform(urlPlatform)
    }

    suspend fun getUrlPlatform(): UrlPriorityPlatform {
        return settingsUseCase.getUrlPlatform()
    }

}