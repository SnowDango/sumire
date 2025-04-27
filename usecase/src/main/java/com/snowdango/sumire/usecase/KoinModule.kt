package com.snowdango.sumire.usecase

import com.snowdango.sumire.usecase.api.SongLinkApiUseCase
import com.snowdango.sumire.usecase.db.AlbumsUseCase
import com.snowdango.sumire.usecase.db.AppSongKeyUseCase
import com.snowdango.sumire.usecase.db.ArtistsUseCase
import com.snowdango.sumire.usecase.db.HistoriesUseCase
import com.snowdango.sumire.usecase.db.SongsUseCase
import com.snowdango.sumire.usecase.db.TasksUseCase
import com.snowdango.sumire.usecase.setting.SettingsUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory<SongLinkApiUseCase> { SongLinkApiUseCase() }
    factory<AppSongKeyUseCase> { AppSongKeyUseCase() }
    factory<HistoriesUseCase> { HistoriesUseCase() }
    factory<SongsUseCase> { SongsUseCase() }
    factory<AlbumsUseCase> { AlbumsUseCase() }
    factory<ArtistsUseCase> { ArtistsUseCase() }
    factory<TasksUseCase> { TasksUseCase() }
    factory<SettingsUseCase> { SettingsUseCase(get()) }
}
