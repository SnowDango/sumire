package com.snowdango.sumire.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.entity.PlayingSongData
import com.snowdango.sumire.data.entity.SongData
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class SongListenerService: NotificationListenerService() {

    private val songSharedFlow: PlayingSongSharedFlow by inject()

    private val channelId = "sumire_song_listener"
    private val channelName = "SumireSongListener"
    private val notificationId = 234234423

    override fun onCreate() {
        super.onCreate()
        initMediaMetadata()
    }

    override fun onBind(intent: Intent?): IBinder? {
        startForeground()
        return super.onBind(intent)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun startForeground(){
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        ).also {
            it.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).also {
            it.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setWhen(System.currentTimeMillis())
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(notificationId, notification)
        } else {
            startForeground(notificationId, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let {
            if(it.packageName == "com.apple.android.music"){
                syncMediaMetadata(it.packageName)
            }
        }
    }

    private fun initMediaMetadata(){
        getSystemService(MediaSessionManager::class.java)?.let { mediaSessionManager ->
            val componentName = ComponentName(this@SongListenerService, SongListenerService::class.java)
            mediaSessionManager.getActiveSessions(componentName).forEach { mediaController ->
                mediaController.playbackState?.isActive?.let {
                    if(it){
                        syncMediaMetadata(mediaController.packageName)
                    }
                }
            }
        }
    }

    private fun syncMediaMetadata(packageName: String) {
        getSystemService(MediaSessionManager::class.java)?.let { mediaSessionManager ->
            val componentName = ComponentName(this@SongListenerService, SongListenerService::class.java)
            MainScope().launch {
                mediaSessionManager.getActiveSessions(componentName)
                    .find { it.packageName == packageName }?.let {
                        it.metadata?.let { metadata ->
                            songSharedFlow.changeSong(
                                PlayingSongData(
                                    SongData(
                                        title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE),
                                        artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST),
                                        album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM),
                                        app = MusicApp.APPLE_MUSIC,
                                        artwork = metadata.getBitmap(MediaMetadata.METADATA_KEY_ART)
                                            ?: metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART),
                                    ),
                                    isActive = it.playbackState?.isActive ?: false
                                )
                            )
                        }
                    }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}