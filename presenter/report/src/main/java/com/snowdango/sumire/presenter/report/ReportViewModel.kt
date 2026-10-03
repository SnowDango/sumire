package com.snowdango.sumire.presenter.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowdango.sumire.model.GetReportModel
import com.snowdango.sumire.ui.viewdata.MonthlyReportViewData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReportViewModel : ViewModel(), KoinComponent {

    private val getReportModel: GetReportModel by inject()

    // null の間は読み込み中
    val monthlyReport: StateFlow<MonthlyReportViewData?> = getReportModel.getCurrentMonthReportFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )
}
