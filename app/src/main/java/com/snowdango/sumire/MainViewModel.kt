package com.snowdango.sumire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowdango.sumire.model.SettingsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel : ViewModel(), KoinComponent {

    private val settingsModel: SettingsModel by inject()

    private val _isShowPermissionDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isShowPermissionDialog: StateFlow<Boolean> = _isShowPermissionDialog.asStateFlow()

    private val _isShowNotificationPermissionDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isShowNotificationDialog: StateFlow<Boolean> = _isShowNotificationPermissionDialog.asStateFlow()

    init {
        viewModelScope.launch {
            _isShowNotificationPermissionDialog.value = settingsModel.getIsFirstTime()
        }
    }

    fun setIsShowPermissionDialog(isShow: Boolean) {
        _isShowPermissionDialog.value = isShow
    }

    fun setIsNotificationPermissionDialog(isShow: Boolean) {
        _isShowNotificationPermissionDialog.value = isShow
    }

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        viewModelScope.launch {
            settingsModel.setIsFirstTime(isFirstTime)
        }
    }
}
