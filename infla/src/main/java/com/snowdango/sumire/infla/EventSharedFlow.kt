package com.snowdango.sumire.infla

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn

class EventSharedFlow {

    private val _eventFlow = MutableSharedFlow<SharedEvent>()

    suspend fun postEvent(event: SharedEvent){
        _eventFlow.emit(event)
    }

    fun subscribe(scope: CoroutineScope, onConsume: (SharedEvent) -> Unit) {
        _eventFlow.onEach {
            onConsume(it)
        }.launchIn(scope)
    }

    abstract class SharedEvent {
        data object ChangeCurrentSong: SharedEvent()
    }

}