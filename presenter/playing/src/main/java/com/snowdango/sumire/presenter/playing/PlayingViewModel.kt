package com.snowdango.sumire.presenter.playing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowdango.sumire.data.entity.PlayingSongData
import com.snowdango.sumire.infla.EventSharedFlow
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayingViewModel(
    private val playingSongSharedFlow: PlayingSongSharedFlow,
    private val eventSharedFlow: EventSharedFlow,
): ViewModel() {

    private val _currentPlayingSong = MutableStateFlow<PlayingSongData?>(value = null)
    val currentPlayingSong: StateFlow<PlayingSongData?> = _currentPlayingSong.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = null,
    )

    init {
        refreshCurrentPlayingSong()
        eventSharedFlow.subscribe(viewModelScope){
            when(it){
                EventSharedFlow.SharedEvent.ChangeCurrentSong -> {
                    refreshCurrentPlayingSong()
                }
            }
        }
    }

    fun refreshCurrentPlayingSong(){
        viewModelScope.launch {
            _currentPlayingSong.emit(
                playingSongSharedFlow.getCurrentPlayingSong()
            )
        }
    }

}