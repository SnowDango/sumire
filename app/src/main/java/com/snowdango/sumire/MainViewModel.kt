package com.snowdango.sumire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MainViewModel : ViewModel() {

    private val _dateChangeFlow = flow {
        while (currentCoroutineContext().isActive) {
            val date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            emit(date)
            delay(60000)
        }
    }
    val dateChangeFlow = _dateChangeFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    )

}