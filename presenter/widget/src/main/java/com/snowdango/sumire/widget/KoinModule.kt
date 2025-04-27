package com.snowdango.sumire.widget

import org.koin.dsl.module

val widgetModule = module {
    single<WidgetViewModel> { WidgetViewModel(get()) }
    single<SmallArtworkWidget> { SmallArtworkWidget() }
}
