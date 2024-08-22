package com.snowdango.sumire

import android.app.Application
import com.snowdango.sumire.infla.EventSharedFlow
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import com.snowdango.sumire.presenter.playing.playingKoinModule
import com.snowdango.sumire.repository.SongLinkApi
import com.snowdango.sumire.repository.SongsDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class SumireApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@SumireApp)
            modules(sharedModule, playingKoinModule)
        }
    }

    private val sharedModule = module {
        single<SongLinkApi> { SongLinkApi() }
        single<SongsDatabase> { SongsDatabase.getInstance(get()) }
        single<EventSharedFlow> { EventSharedFlow() }
        single<PlayingSongSharedFlow> { PlayingSongSharedFlow(get(), get()) }
    }
}