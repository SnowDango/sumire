package com.snowdango.presenter.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.snowdango.sumire.data.util.LocalDateTimeFormatType
import com.snowdango.sumire.model.GetHistoriesModel
import com.snowdango.sumire.model.GetSongsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HistoryViewModel : ViewModel(), KoinComponent {

    private val getHistoriesModel: GetHistoriesModel by inject()
    private val getSongsModel: GetSongsModel by inject()

    val getHistories = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 50,
        )
    ) {
        getHistoriesModel.getPagingHistorySongs()
    }.flow.map { pagingData ->
        pagingData.map {
            getHistoriesModel.convertHistorySongToSongCardViewData(
                it,
                LocalDateTimeFormatType.ONLY_TIME
            )
        }
    }.cachedIn(viewModelScope)

    private var searchText: String = ""
    val searchHistories = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 50,
        )
    ) {
        getHistoriesModel.getPagingSearchHistorySongs(getSearchText())
    }.flow.map { pagingData ->
        pagingData.map {
            getHistoriesModel.convertHistorySongToSongCardViewData(
                it,
                LocalDateTimeFormatType.ONLY_TIME,
            )
        }
    }.cachedIn(viewModelScope)

    fun setSearchText(searchText: String) {
        this.searchText = searchText
    }

    fun getSearchText(): String {
        return searchText
    }

    private val _suggestSearchTitleListFlow = MutableStateFlow<List<String>>(value = listOf())
    val suggestSearchTitleListFlow: StateFlow<List<String>> = _suggestSearchTitleListFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = listOf()
    )

    fun getSuggestSearchTitle(currentSearchText: String) = viewModelScope.launch {
        _suggestSearchTitleListFlow.emit(
            getSongsModel.getSearchTitleList(currentSearchText)
        )
    }


}