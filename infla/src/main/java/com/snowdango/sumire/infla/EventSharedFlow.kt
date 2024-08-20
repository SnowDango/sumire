package com.snowdango.sumire.infla

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class EventSharedFlow {

    private val _eventFlow = MutableSharedFlow<SharedEvent>()
    val eventFlow: SharedFlow<SharedEvent> = _eventFlow



    abstract class SharedEvent {
        data object ChangeCurrentSong: SharedEvent()
    }

}