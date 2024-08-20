package com.snowdango.sumire.infla

import com.snowdango.sumire.data.entity.PlayingSongData
import com.snowdango.sumire.data.entity.SongData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class PlayingSongSharedFlow {

    private val _playingSongFlow = MutableStateFlow<PlayingSongData?>(value = null)
    val playingSongFlow: StateFlow<PlayingSongData?> = _playingSongFlow.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null,
    )

    private var playingSong: PlayingSongData? = null

    suspend fun changeSong(playingSongData: PlayingSongData?) {
        _playingSongFlow.emit(playingSongData)
        playingSong = playingSongData
    }


}