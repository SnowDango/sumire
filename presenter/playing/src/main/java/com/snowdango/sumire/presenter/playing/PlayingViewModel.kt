package com.snowdango.sumire.presenter.playing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowdango.sumire.data.entity.playing.PlayingSongData
import com.snowdango.sumire.infla.EventSharedFlow
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import com.snowdango.sumire.model.GetHistoriesModel
import com.snowdango.sumire.ui.viewdata.SongCardViewData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayingViewModel(
    private val playingSongSharedFlow: PlayingSongSharedFlow,
    private val eventSharedFlow: EventSharedFlow,
) : ViewModel(), KoinComponent {

    private val getHistoriesModel: GetHistoriesModel by inject()

    private val _currentPlayingSong = MutableStateFlow<PlayingSongData?>(value = null)
    val currentPlayingSong: StateFlow<PlayingSongData?> = _currentPlayingSong.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = null,
    )

    private val _recentHistory = getHistoriesModel.getRecentHistoriesSongFlow(size = 10)
    val recentHistories: StateFlow<List<SongCardViewData>> = _recentHistory.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = listOf()
    )

    init {
        refreshCurrentPlayingSong()
        eventSharedFlow.subscribe(viewModelScope) {
            when (it) {
                EventSharedFlow.SharedEvent.ChangeCurrentSong -> {
                    refreshCurrentPlayingSong()
                }
            }
        }
    }

    private fun refreshCurrentPlayingSong() {
        viewModelScope.launch {
            _currentPlayingSong.emit(
                playingSongSharedFlow.getCurrentPlayingSong()
            )
        }
    }

}