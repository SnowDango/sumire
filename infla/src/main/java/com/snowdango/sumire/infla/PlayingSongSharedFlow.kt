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
    private var playingSong: Pair<Long, PlayingSongData>? = null

    @Volatile
    var listener: ((playingSong: PlayingSongData?) -> Unit)? = null
    private var isWaitingTime: Boolean = false
    private val playingSongMutex = Mutex()
    private val isWaitingMutex = Mutex()

    @Suppress("CyclomaticComplexMethod")
    suspend fun changeSong(queueId: Long?, playingSongData: PlayingSongData?) {
        withContext(Dispatchers.IO) {
            var type: PlayingSongChangeType = PlayingSongChangeType.NONE
            var notifyListener = false
            var listenerValue: PlayingSongData? = null
            playingSongMutex.withLock {
                val currentQueueId = playingSong?.first
                if (currentQueueId != queueId) {
                    // change playingSong
                    playingSong = if (queueId != null && playingSongData != null) {
                        Pair(queueId, playingSongData)
                    } else {
                        null
                    }
                    notifyListener = true
                    listenerValue = playingSong?.second
                    type = if (queueId != null && playingSongData != null) {
                        if (playingSong?.second?.songData?.artwork == null) {
                            PlayingSongChangeType.CHANGE
                        } else {
                            PlayingSongChangeType.DATA_COMPLETE
                        }
                    } else {
                        PlayingSongChangeType.NONE
                    }
                } else if (playingSong != null && playingSongData != null) {
                    if (playingSong?.second?.isActive != playingSongData.isActive) {
                        // 　update isActive
                        playingSong = if (queueId != null) {
                            Pair(
                                queueId,
                                playingSongData.copy(playTime = playingSong!!.second.playTime),
                            )
                        } else {
                            null
                        }
                        notifyListener = true
                        listenerValue = playingSong?.second
                        type = PlayingSongChangeType.CHANGE_ACTIVE
                    } else if (
                        playingSong?.second?.songData?.artwork == null &&
                        playingSongData.songData.artwork != null
                    ) {
                        // update artwork
                        playingSong = if (queueId != null) {
                            Pair(
                                queueId,
                                playingSongData.copy(playTime = playingSong!!.second.playTime),
                            )
                        } else {
                            null
                        }
                        notifyListener = true
                        listenerValue = playingSong?.second
                        type = PlayingSongChangeType.DATA_COMPLETE
                    }
                }
            }
            if (notifyListener) {
                listener?.invoke(listenerValue)
            }
            if (type != PlayingSongChangeType.NONE) {
                changedPlayingSong(
                    type,
                    queueId,
                )
            }
        }
    }

    private suspend fun changedPlayingSong(
        type: PlayingSongChangeType,
        queueId: Long?,
    ) {
        eventSharedFlow.postEvent(
            EventSharedFlow.SharedEvent.ChangeCurrentSong,
        )
        val current = playingSong
        if (type != PlayingSongChangeType.CHANGE_ACTIVE && type != PlayingSongChangeType.NONE) {
            var afterCheckType = AfterCheckType.NONE
            isWaitingMutex.withLock {
                when (type) {
                    PlayingSongChangeType.CHANGE -> {
                        isWaitingTime = true
                        afterCheckType = AfterCheckType.WAIT
                    }

                    PlayingSongChangeType.DATA_COMPLETE -> {
                        isWaitingTime = false
                        afterCheckType = AfterCheckType.COMPLETE
                    }

                    else -> {}
                }
            }
            when (afterCheckType) {
                AfterCheckType.WAIT -> waitMetadata(queueId)
                AfterCheckType.COMPLETE -> current?.let { metaDataComplete(it.second) }
                AfterCheckType.NONE -> {}
            }
        }
    }

    @Suppress("MagicNumber")
    private suspend fun waitMetadata(queueId: Long?) {
        if (queueId == null) return
        var isComplete = false
        withContext(Dispatchers.Default) {
            delay(10_000)
            val curernt = playingSong
            withContext(Dispatchers.IO) {
                isWaitingMutex.withLock {
                    if (isWaitingTime) {
                        curernt?.let {
                            if (it.first == queueId) {
                                isWaitingTime = false
                                isComplete = true
                            }
                        }
                    }
                }
            }
            if (isComplete) {
                curernt?.let {
                    metaDataComplete(it.second)
                }
            }
        }
    }

    private suspend fun metaDataComplete(playingSongData: PlayingSongData) {
        withContext(Dispatchers.Default) {
            Log.d(
                "CurrentPlayingSong",
                "DataComplete \nhasArtwork = ${playingSong?.second?.songData?.artwork != null}",
            )
            saveModel.saveSong(playingSongData)
        }
    }

    fun getCurrentPlayingSong(): PlayingSongData? {
        return playingSong?.second
    }

    enum class PlayingSongChangeType {
        DATA_COMPLETE,
        CHANGE,
        CHANGE_ACTIVE,
        NONE,
    }

    enum class AfterCheckType {
        COMPLETE,
        WAIT,
        NONE,
    }

    interface ChangeListener {
        fun onChanged()
    }
}
