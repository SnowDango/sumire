package com.snowdango.presenter.history

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val historyKoinModule = module {
    viewModel { HistoryViewModel() }
}
