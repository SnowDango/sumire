package com.snowdango.sumire.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.snowdango.sumire.BuildConfig
import com.snowdango.sumire.R
import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.entity.playing.PlayingSongData
import com.snowdango.sumire.data.entity.playing.SongData
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import com.snowdango.sumire.logging.Logging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.android.ext.android.inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SongListenerService : NotificationListenerService() {

    private val songSharedFlow: PlayingSongSharedFlow by inject()
    private val appScope: CoroutineScope by inject()

    private val channelId = "sumire_song_listener"
    private val channelName = "SumireSongListener"
    private val notificationId = 234234423

    // 通知を出し直さずに一時停止・曲送りするアプリもあるので、対象アプリの MediaSession の変化も直接受け取る。
    // コールバックはすべてメインスレッドで受けるので、watchedSessions はメインスレッドからしか触らない
    private val mainHandler = Handler(Looper.getMainLooper())
    private val watchedSessions = mutableMapOf<MediaSession.Token, WatchedSession>()
    private val activeSessionsListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        watchMediaSessions(controllers.orEmpty())
    }

    override fun onBind(intent: Intent?): IBinder? {
        startForeground()
        return super.onBind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // MainActivity から startForegroundService() で起動されたときも
        // 5 秒以内に startForeground() を呼ぶ必要がある
        startForeground()
        return START_NOT_STICKY
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        // getActiveSessions() はリスナーが接続されてから呼ぶ
        initMediaMetadata()
        startWatchingMediaSessions()
    }

    override fun onListenerDisconnected() {
        stopWatchingMediaSessions()
        super.onListenerDisconnected()
    }

    override fun onDestroy() {
        stopWatchingMediaSessions()
        super.onDestroy()
    }

    private fun startForeground() {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_NONE,
        ).also {
            it.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).also {
            it.createNotificationChannel(channel)
        }
        // smallIcon とタイトルが無いと、システムが汎用の「実行中」通知に差し替えてしまう
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.app_name))
            .setCategory(Notification.CATEGORY_SERVICE)
            .setWhen(System.currentTimeMillis())
            .setOngoing(true)
            .build()

        startForeground(notificationId, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val packageName = sbn?.packageName ?: return
        if (isTargetMusicApp(packageName)) {
            syncMediaMetadata(packageName)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        val packageName = sbn?.packageName ?: return
        if (isTargetMusicApp(packageName)) {
            clearIfSessionGone(packageName)
        }
    }

    private fun isTargetMusicApp(packageName: String): Boolean =
        MusicApp.entries.any { it.packageName.isNotEmpty() && it.packageName == packageName }

    private fun startWatchingMediaSessions() {
        val mediaSessionManager = getSystemService(MediaSessionManager::class.java) ?: return
        val componentName = ComponentName(this@SongListenerService, SongListenerService::class.java)
        try {
            // 同じリスナーを 2 回登録しても無視されるので、再接続で呼ばれても重複しない
            mediaSessionManager.addOnActiveSessionsChangedListener(
                activeSessionsListener,
                componentName,
                mainHandler,
            )
            watchMediaSessions(mediaSessionManager.getActiveSessions(componentName))
        } catch (e: SecurityException) {
            // 通知へのアクセスが外された直後などは呼べない。通知からの検知だけで動く
            Log.e(LOG_TAG, "failed to watch media sessions", e)
        }
    }

    private fun stopWatchingMediaSessions() {
        getSystemService(MediaSessionManager::class.java)
            ?.removeOnActiveSessionsChangedListener(activeSessionsListener)
        watchMediaSessions(emptyList())
    }

    /**
     * 対象アプリのセッションにだけコールバックを登録し、無くなったセッションのコールバックは外す
     */
    private fun watchMediaSessions(controllers: List<MediaController>) {
        val targets = controllers.filter { isTargetMusicApp(it.packageName) }
            .associateBy { it.sessionToken }
        (watchedSessions.keys - targets.keys).forEach { token ->
            watchedSessions.remove(token)?.let { it.controller.unregisterCallback(it.callback) }
        }
        (targets - watchedSessions.keys).forEach { (token, controller) ->
            val callback = SessionCallback(controller.packageName)
            controller.registerCallback(callback, mainHandler)
            watchedSessions[token] = WatchedSession(controller, callback)
        }
    }

    private fun initMediaMetadata() {
        getSystemService(MediaSessionManager::class.java)?.let { mediaSessionManager ->
            val componentName =
                ComponentName(this@SongListenerService, SongListenerService::class.java)
            mediaSessionManager.getActiveSessions(componentName).forEach { mediaController ->
                if (mediaController.playbackState?.isActive == true) {
                    syncMediaMetadata(mediaController.packageName)
                }
            }
        }
    }

    private fun syncMediaMetadata(packageName: String) {
        val mediaSessionManager = getSystemService(MediaSessionManager::class.java) ?: return
        val componentName = ComponentName(this@SongListenerService, SongListenerService::class.java)
        appScope.launch {
            try {
                val controller = mediaSessionManager.getActiveSessions(componentName)
                    .find { it.packageName == packageName } ?: return@launch
                val musicApp = MusicApp.entries.firstOrNull { it.packageName == packageName }
                    ?: return@launch
                val metadata = controller.metadata
                songSharedFlow.changeSong(
                    queueId = resolveQueueId(controller, metadata),
                    playingSongData = metadata?.let { createPlayingSongData(it, controller, musicApp) },
                )
                if (BuildConfig.DEBUG) {
                    loggingMediaController(metadata, controller)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(LOG_TAG, "failed to sync media metadata for $packageName", e)
            }
        }
    }

    /**
     * 通知が消えたあと、そのアプリのセッションも無くなっていれば再生中の曲をクリアする。
     * 曲の切り替え時に通知が一瞬消えるだけのケースではセッションが残るので何もしない。
     */
    private fun clearIfSessionGone(packageName: String) {
        val mediaSessionManager = getSystemService(MediaSessionManager::class.java) ?: return
        val componentName = ComponentName(this@SongListenerService, SongListenerService::class.java)
        appScope.launch {
            try {
                val stillActive = mediaSessionManager.getActiveSessions(componentName)
                    .any { it.packageName == packageName }
                val current = songSharedFlow.getCurrentPlayingSong()
                if (!stillActive && current?.songData?.app?.packageName == packageName) {
                    songSharedFlow.changeSong(queueId = null, playingSongData = null)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(LOG_TAG, "failed to check media session for $packageName", e)
            }
        }
    }

    /**
     * 曲を識別するためのキー。
     * 再生中のキューアイテム ID を優先し、無ければキュー先頭、それも無ければ mediaId から導出する。
     * キュー先頭は「再生中の曲」とは限らないので、あくまでフォールバック。
     */
    private fun resolveQueueId(controller: MediaController, metadata: MediaMetadata?): Long? {
        val activeQueueItemId = controller.playbackState?.activeQueueItemId
            ?.takeIf { it != MediaSession.QueueItem.UNKNOWN_ID.toLong() }
        return activeQueueItemId
            ?: controller.queue?.firstOrNull()?.queueId
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_MEDIA_ID)?.hashCode()?.toLong()
    }

    @OptIn(ExperimentalTime::class)
    private fun createPlayingSongData(
        metadata: MediaMetadata,
        mediaController: MediaController,
        musicApp: MusicApp,
    ): PlayingSongData? {
        // タイトルが無いものは曲として扱えない
        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: return null
        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST).orEmpty()
        val album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM).orEmpty()
        // mediaId を返さないアプリ向けに、曲を同定できるキーを代わりに組み立てる
        val mediaId = metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_ID)
            ?: "${musicApp.platform}:$title:$artist:$album"
        return PlayingSongData(
            SongData(
                title = title,
                artist = artist,
                album = album,
                app = musicApp,
                artwork = metadata.getBitmap(MediaMetadata.METADATA_KEY_ART)
                    ?: metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART),
                mediaId = mediaId,
            ),
            isActive = mediaController.playbackState?.isActive ?: false,
            playTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        )
    }

    private fun loggingMediaNotification(
        packageName: String,
    ) {
        getSystemService(MediaSessionManager::class.java)?.let { mediaSessionManager ->
            val componentName =
                ComponentName(this@SongListenerService, SongListenerService::class.java)
            appScope.launch {
                mediaSessionManager.getActiveSessions(componentName)
                    .find { it.packageName == packageName }?.let {
                        loggingMediaController(it.metadata, it)
                    }
            }
        }
    }

    private fun loggingMediaController(
        metadata: MediaMetadata?,
        mediaController: MediaController?,
    ) {
        metadata?.let { Logging.loggingMetaData(it) }
        mediaController?.playbackState?.let {
            Logging.loggingPlaybackState(it.state)
            Logging.loggingPlaybackAction(it.actions)
        }
    }

    private inner class SessionCallback(
        private val packageName: String,
    ) : MediaController.Callback() {
        private var lastIsActive: Boolean? = null
        private var lastQueueItemId: Long? = null

        // 状態の変化は通知と両方から届くことがあるが、changeSong() は変化が無ければ何もしない。
        // 再生位置が進むたびに PlaybackState を更新するアプリもあるので、再生中かどうかと
        // 再生中のキューアイテムが変わったときだけ読み直す
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            val isActive = state?.isActive
            val queueItemId = state?.activeQueueItemId
            if (isActive == lastIsActive && queueItemId == lastQueueItemId) return
            lastIsActive = isActive
            lastQueueItemId = queueItemId
            syncMediaMetadata(packageName)
        }

        override fun onMetadataChanged(metadata: MediaMetadata?) {
            syncMediaMetadata(packageName)
        }

        override fun onSessionDestroyed() {
            clearIfSessionGone(packageName)
        }
    }

    private class WatchedSession(
        val controller: MediaController,
        val callback: MediaController.Callback,
    )

    companion object {
        private const val LOG_TAG = "SongListenerService"
    }
}
