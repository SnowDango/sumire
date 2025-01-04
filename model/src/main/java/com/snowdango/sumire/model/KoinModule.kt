package com.snowdango.sumire.model

import org.koin.dsl.module


val modelModule = module {
    factory<SaveModel> { SaveModel() }
    factory<GetHistoriesModel> { GetHistoriesModel() }
    factory<ShareSongModel> { ShareSongModel() }
    factory<SettingsModel> { SettingsModel() }
    factory<GetSongsModel> { GetSongsModel() }
}