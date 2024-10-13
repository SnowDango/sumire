package com.snowdango.presenter.history

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val historyKoinModule = module {
    viewModel { HistoryViewModel() }
}