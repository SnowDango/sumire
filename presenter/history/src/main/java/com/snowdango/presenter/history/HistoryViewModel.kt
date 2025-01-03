package com.snowdango.presenter.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.snowdango.sumire.data.util.LocalDateTimeFormatType
import com.snowdango.sumire.model.GetHistoriesModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HistoryViewModel : ViewModel(), KoinComponent {

    private val getHistoriesModel: GetHistoriesModel by inject()

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

    private val _searchTextLiveData = MutableLiveData("")
    val searchTextLiveData: LiveData<String> = _searchTextLiveData

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

    fun setSearchText(searchText: String) = viewModelScope.launch {
        _searchTextLiveData.value = searchText
    }

    private fun getSearchText(): String {
        return _searchTextLiveData.value.toString()
    }


}