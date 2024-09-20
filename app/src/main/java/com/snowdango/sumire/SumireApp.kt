package com.snowdango.sumire

import android.app.Application
import com.snowdango.sumire.infla.EventSharedFlow
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import com.snowdango.sumire.model.modelModule
import com.snowdango.sumire.presenter.playing.playingKoinModule
import com.snowdango.sumire.repository.SongLinkApi
import com.snowdango.sumire.repository.SongsDatabase
import com.snowdango.sumire.usecase.useCaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class SumireApp : Application() {

    override fun onCreate() {
        super.onCreate()
        GlobalContext.getOrNull() ?: startKoin {
            androidLogger()
            androidContext(this@SumireApp)
            modules(sharedModule, playingKoinModule, modelModule, useCaseModule)
        }
    }

    private val sharedModule = module {
        single<SongLinkApi> { SongLinkApi() }
        single<SongsDatabase> { SongsDatabase.getInstance(get()) }
        single<EventSharedFlow> { EventSharedFlow() }
        single<PlayingSongSharedFlow> { PlayingSongSharedFlow() }
    }
}
