package com.snowdango.sumire

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.snowdango.presenter.history.historyKoinModule
import com.snowdango.sumire.infla.EventSharedFlow
import com.snowdango.sumire.infla.LogEvent
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import com.snowdango.sumire.model.modelModule
import com.snowdango.sumire.presenter.playing.playingKoinModule
import com.snowdango.sumire.repository.SongLinkApi
import com.snowdango.sumire.repository.SongsDatabase
import com.snowdango.sumire.settings.settingsModule
import com.snowdango.sumire.usecase.useCaseModule
import com.snowdango.sumire.widget.widgetModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class SumireApp : Application() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val playingSongSharedFlow: PlayingSongSharedFlow by inject()
    private val eventSharedFlow: EventSharedFlow by inject()
    private val logEvent: LogEvent by inject()

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
        initEventListener()
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
        single<CoroutineScope> { CoroutineScope(Dispatchers.Default) }
        factory<LogEvent> { LogEvent(get()) }
    }

    private fun initEventListener() {
        eventSharedFlow.subscribe(CoroutineScope(Dispatchers.Default)) { event ->
            when (event) {
                is EventSharedFlow.SharedEvent.ChangeCurrentSong -> {
                    val currentSong =
                        playingSongSharedFlow.getCurrentPlayingSong() ?: return@subscribe
                    logEvent.sendEvent(
                        event = LogEvent.Event.SAVE_HISTORY_EVENT,
                        params = mapOf(
                            LogEvent.Param.PARAM_TITLE to currentSong.songData.title,
                            LogEvent.Param.PARAM_ALBUM to currentSong.songData.album,
                            LogEvent.Param.PARAM_ARTIST to currentSong.songData.artist,
                            LogEvent.Param.PARAM_APP_NAME to currentSong.songData.app.platform,
                        ),
                    )
                }
            }
        }
    }
}
