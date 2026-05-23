package com.snowdango.sumire.infla

import android.util.Log
import com.snowdango.sumire.data.entity.playing.PlayingSongData
import com.snowdango.sumire.model.SaveModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayingSongSharedFlow : KoinComponent {

    private val eventSharedFlow: EventSharedFlow by inject()
    private val saveModel: SaveModel by inject()

    @Volatile
    private var playingSong: PlayingState? = null

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
        playingSong = if (queueId != null && playingSongData != null) {
            PlayingState(queueId, playingSongData)
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
        playingSong = PlayingState(
            queueId,
            playingSongData.copy(playTime = current.data.playTime),
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
        eventSharedFlow.postEvent(EventSharedFlow.SharedEvent.ChangeCurrentSong)
        if (type == PlayingSongChangeType.CHANGE_ACTIVE) return

        val current = playingSong
        when (updateWaitingState(type)) {
            AfterCheckType.WAIT -> waitMetadata(queueId)
            AfterCheckType.COMPLETE -> current?.let { metaDataComplete(it.data) }
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
            current?.let { metaDataComplete(it.data) }
        }
    }

    private suspend fun metaDataComplete(playingSongData: PlayingSongData) {
        withContext(Dispatchers.Default) {
            Log.d(
                LOG_TAG,
                "DataComplete \nhasArtwork = ${playingSong?.data?.songData?.artwork != null}",
            )
            saveModel.saveSong(playingSongData)
        }
    }

    private data class PlayingState(
        val queueId: Long,
        val data: PlayingSongData,
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
    }
}
