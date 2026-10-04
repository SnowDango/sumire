package com.snowdango.sumire.infla

import android.os.SystemClock
import android.util.Log
import com.snowdango.sumire.data.entity.playing.PlayingSongData
import com.snowdango.sumire.model.SaveModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayingSongSharedFlow : KoinComponent {

    private val eventSharedFlow: EventSharedFlow by inject()
    private val saveModel: SaveModel by inject()
    private val appScope: CoroutineScope by inject()

    // 再生時間は端末のスリープ中も進み、時刻の変更でずれない elapsedRealtime で測る
    private val clock: () -> Long = SystemClock::elapsedRealtime

    @Volatile
    private var playingSong: PlayingState? = null

    // 再生中の区間を途中で区切って書き込むタイマー。playingSongMutex の中でだけ触る
    private var checkpointJob: Job? = null

    @Volatile
    var listener: ((playingSong: PlayingSongData?) -> Unit)? = null

    private var isWaitingTime: Boolean = false
    private val playingSongMutex = Mutex()
    private val isWaitingMutex = Mutex()

    suspend fun changeSong(queueId: Long?, playingSongData: PlayingSongData?) {
        withContext(Dispatchers.IO) {
            val result = updateState(queueId, playingSongData)
            if (result.notifyListener) {
                listener?.invoke(result.notifyValue)
                // 曲が null になった(停止した)ときも画面側に通知する
                eventSharedFlow.postEvent(EventSharedFlow.SharedEvent.ChangeCurrentSong)
            }
            if (result.type != PlayingSongChangeType.NONE) {
                handleStateChange(result.type, queueId)
            }
        }
    }

    fun getCurrentPlayingSong(): PlayingSongData? = playingSong?.data

    private suspend fun updateState(
        queueId: Long?,
        playingSongData: PlayingSongData?,
    ): UpdateResult = playingSongMutex.withLock {
        val current = playingSong
        when {
            current?.queueId != queueId ->
                updateOnQueueChange(queueId, playingSongData)
            current != null && queueId != null && playingSongData != null ->
                updateOnSameQueue(current, queueId, playingSongData)
            else ->
                UpdateResult.NONE
        }
    }

    private fun updateOnQueueChange(
        queueId: Long?,
        playingSongData: PlayingSongData?,
    ): UpdateResult {
        // 前の曲はここで終わりなので、再生時間の計測を締める
        playingSong?.let { finishListening(it.listeningSession) }
        playingSong = if (queueId != null && playingSongData != null) {
            val listeningSession = ListeningSession(clock).also {
                if (playingSongData.isActive) startListening(it)
            }
            PlayingState(queueId, playingSongData, listeningSession)
        } else {
            null
        }
        val type = when {
            queueId == null || playingSongData == null -> PlayingSongChangeType.NONE
            playingSongData.songData.artwork == null -> PlayingSongChangeType.CHANGE
            else -> PlayingSongChangeType.DATA_COMPLETE
        }
        return UpdateResult(
            type = type,
            notifyListener = true,
            notifyValue = playingSong?.data,
        )
    }

    private fun updateOnSameQueue(
        current: PlayingState,
        queueId: Long,
        playingSongData: PlayingSongData,
    ): UpdateResult {
        val updatedType = when {
            current.data.isActive != playingSongData.isActive ->
                PlayingSongChangeType.CHANGE_ACTIVE
            current.data.songData.artwork == null && playingSongData.songData.artwork != null ->
                PlayingSongChangeType.DATA_COMPLETE
            else ->
                return UpdateResult.NONE
        }
        if (updatedType == PlayingSongChangeType.CHANGE_ACTIVE) {
            changeListeningActive(current.listeningSession, playingSongData.isActive)
        }
        playingSong = PlayingState(
            queueId,
            playingSongData.copy(playTime = current.data.playTime),
            // 同じ曲のまま data だけ変わったので、再生時間の計測は引き継ぐ
            current.listeningSession,
        )
        return UpdateResult(
            type = updatedType,
            notifyListener = true,
            notifyValue = playingSong?.data,
        )
    }

    private suspend fun handleStateChange(
        type: PlayingSongChangeType,
        queueId: Long?,
    ) {
        if (type == PlayingSongChangeType.CHANGE_ACTIVE) return

        val current = playingSong
        when (updateWaitingState(type)) {
            AfterCheckType.WAIT -> waitMetadata(queueId)
            AfterCheckType.COMPLETE -> current?.let { metaDataComplete(it) }
            AfterCheckType.NONE -> {}
        }
    }

    private suspend fun updateWaitingState(type: PlayingSongChangeType): AfterCheckType =
        isWaitingMutex.withLock {
            when (type) {
                PlayingSongChangeType.CHANGE -> {
                    isWaitingTime = true
                    AfterCheckType.WAIT
                }
                PlayingSongChangeType.DATA_COMPLETE -> {
                    isWaitingTime = false
                    AfterCheckType.COMPLETE
                }
                else -> AfterCheckType.NONE
            }
        }

    private suspend fun waitMetadata(queueId: Long?) {
        if (queueId == null) return
        delay(METADATA_WAIT_TIMEOUT_MS)
        val current = playingSong
        val isComplete = isWaitingMutex.withLock {
            if (isWaitingTime && current?.queueId == queueId) {
                isWaitingTime = false
                true
            } else {
                false
            }
        }
        if (isComplete) {
            current?.let { metaDataComplete(it) }
        }
    }

    private suspend fun metaDataComplete(playingState: PlayingState) {
        val listeningSession = playingState.listeningSession
        listeningSession.markSaveRequested()
        var historyId: Long? = null
        try {
            historyId = withContext(Dispatchers.Default) {
                Log.d(
                    LOG_TAG,
                    "DataComplete \nhasArtwork = ${playingSong?.data?.songData?.artwork != null}",
                )
                saveModel.saveSong(playingState.data)
            }
        } finally {
            // 保存に失敗したときも完了させ、再生時間の書き込み待ちを終わらせる
            listeningSession.historyId.complete(historyId)
        }
    }

    private fun changeListeningActive(listeningSession: ListeningSession, isActive: Boolean) {
        if (isActive) {
            startListening(listeningSession)
        } else {
            stopCheckpoint()
            // プロセスが終了しても一時停止までの分は残るよう、曲の終わりを待たずに書き込む
            addListeningTime(listeningSession, listeningSession.pause())
        }
    }

    private fun finishListening(listeningSession: ListeningSession) {
        stopCheckpoint()
        addListeningTime(listeningSession, listeningSession.finish())
    }

    private fun startListening(listeningSession: ListeningSession) {
        listeningSession.resume()
        // 長い曲を流し続けている途中でプロセスが終了しても、失うのが最大この間隔の分で済むよう区切って書き込む。
        // 再開・曲の切り替えのたびに数え直すので、間隔より短い曲では書き込みは増えない
        checkpointJob?.cancel()
        checkpointJob = appScope.launch {
            while (true) {
                delay(LISTENING_CHECKPOINT_INTERVAL_MS)
                addListeningTime(listeningSession, listeningSession.checkpoint())
            }
        }
    }

    private fun stopCheckpoint() {
        checkpointJob?.cancel()
        checkpointJob = null
    }

    private fun addListeningTime(listeningSession: ListeningSession, listeningMs: Long) {
        if (listeningMs <= 0L) return
        // 履歴の ID が分かるまで (最大 10 秒のアートワーク待ちと song.link API) 待つことがあるので、
        // changeSong の呼び出し元を止めないようアプリスコープで書き込む
        appScope.launch {
            val historyId = listeningSession.historyId.await() ?: return@launch
            try {
                saveModel.addListeningTime(historyId, listeningMs)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(LOG_TAG, "failed to add listening time to history $historyId", e)
            }
        }
    }

    private data class PlayingState(
        val queueId: Long,
        val data: PlayingSongData,
        val listeningSession: ListeningSession,
    )

    private data class UpdateResult(
        val type: PlayingSongChangeType,
        val notifyListener: Boolean,
        val notifyValue: PlayingSongData?,
    ) {
        companion object {
            val NONE = UpdateResult(PlayingSongChangeType.NONE, false, null)
        }
    }

    private enum class PlayingSongChangeType {
        DATA_COMPLETE,
        CHANGE,
        CHANGE_ACTIVE,
        NONE,
    }

    private enum class AfterCheckType {
        COMPLETE,
        WAIT,
        NONE,
    }

    companion object {
        private const val LOG_TAG = "CurrentPlayingSong"
        private const val METADATA_WAIT_TIMEOUT_MS = 10_000L

        // 書き込むたびに Room の Flow / Paging が読み直すので、ふつうの曲の長さより長めにしている
        private const val LISTENING_CHECKPOINT_INTERVAL_MS = 5 * 60_000L
    }
}
