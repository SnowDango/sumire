package com.snowdango.sumire.infla

import com.snowdango.sumire.data.entity.SongData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class PlayingSongSharedFlow {

    private val _playingSongFlow = MutableSharedFlow<SongData?>()
    val playingSongFlow: SharedFlow<SongData?> = _playingSongFlow


    suspend fun changeSong(songData: SongData?) {
        _playingSongFlow.emit(songData)
    }


}