package com.snowdango.sumire.presenter.playing

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val playingKoinModule = module {
    viewModel { PlayingViewModel(get(), get()) }
}