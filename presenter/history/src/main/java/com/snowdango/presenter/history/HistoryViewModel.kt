package com.snowdango.presenter.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.snowdango.sumire.data.util.LocalDateTimeFormatType
import com.snowdango.sumire.model.GetHistoriesModel
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HistoryViewModel : ViewModel(), KoinComponent {

    private val getHistoriesModel: GetHistoriesModel by inject()

    val getHistories = Pager(
        config = PagingConfig(
            pageSize = 100,
            prefetchDistance = 100,
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

}