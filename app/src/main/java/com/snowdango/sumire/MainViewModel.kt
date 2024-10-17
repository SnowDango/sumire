package com.snowdango.sumire

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowdango.sumire.model.SettingsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel : ViewModel(), KoinComponent {

    private val settingsModel: SettingsModel by inject()

    private val _isShowPermissionDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isShowPermissionDialog = _isShowPermissionDialog.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        _isShowPermissionDialog.value,
    )

    private val _isShowNotificationPermissionDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isShowNotificationDialog = _isShowNotificationPermissionDialog.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        _isShowNotificationPermissionDialog.value,
    )

    init {
        viewModelScope.launch {
            _isShowNotificationPermissionDialog.emit(
                settingsModel.getIsFirstTime(),
            )
        }
    }

    fun setIsShowPermissionDialog(isShow: Boolean) {
        viewModelScope.launch {
            _isShowPermissionDialog.emit(isShow)
        }
    }

    fun setIsNotificationPermissionDialog(isShow: Boolean) {
        viewModelScope.launch {
            Log.d("permission", isShow.toString())
            _isShowNotificationPermissionDialog.emit(isShow)
        }
    }

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        viewModelScope.launch {
            settingsModel.setIsFirstTime(isFirstTime)
        }
    }
}
