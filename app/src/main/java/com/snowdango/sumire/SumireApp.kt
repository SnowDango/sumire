package com.snowdango.sumire

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snowdango.presenter.history.historyKoinModule
import com.snowdango.sumire.infla.EventSharedFlow
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import com.snowdango.sumire.model.modelModule
import com.snowdango.sumire.presenter.playing.playingKoinModule
import com.snowdango.sumire.repository.SongLinkApi
import com.snowdango.sumire.repository.SongsDatabase
import com.snowdango.sumire.settings.settingsModule
import com.snowdango.sumire.usecase.useCaseModule
import com.snowdango.sumire.widget.widgetModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class SumireApp : Application() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate() {
        super.onCreate()
        GlobalContext.getOrNull() ?: startKoin {
            androidLogger()
            androidContext(this@SumireApp)
            modules(
                sharedModule,
                playingKoinModule,
                mainModule,
                historyKoinModule,
                settingsModule,
                modelModule,
                useCaseModule,
                widgetModule,
            )
        }
    }

    private val mainModule = module {
        viewModel { MainViewModel() }
    }

    private val sharedModule = module {
        single<SongLinkApi> { SongLinkApi() }
        single<SongsDatabase> { SongsDatabase.getInstance(get()) }
        single<EventSharedFlow> { EventSharedFlow() }
        single<PlayingSongSharedFlow> { PlayingSongSharedFlow() }
        single<DataStore<Preferences>> { dataStore }
    }
}
