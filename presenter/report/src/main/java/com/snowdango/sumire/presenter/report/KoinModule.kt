package com.snowdango.sumire.presenter.report

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val reportKoinModule = module {
    viewModel { ReportViewModel() }
}
