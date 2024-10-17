package com.snowdango.sumire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _isShowPermissionDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isShowPermissionDialog = _isShowPermissionDialog.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        _isShowPermissionDialog.value,
    )

    fun setIsShowPermissionDialog(isShow: Boolean) {
        viewModelScope.launch {
            _isShowPermissionDialog.emit(isShow)
        }
    }
}
