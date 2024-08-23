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

    private var playingSong: Pair<Long, PlayingSongData>? = null
    private var isWaitingTime: Boolean = false
    private val playingSongMutex = Mutex()
    private val isWaitingMutex = Mutex()

    suspend fun changeSong(queueId: Long?, playingSongData: PlayingSongData?) {
        withContext(Dispatchers.IO) {
            var type: PlayingSongChangeType = PlayingSongChangeType.NONE
            playingSongMutex.withLock(playingSongData) {
                val currentQueueId = playingSong?.first
                if (currentQueueId != queueId) {
                    // change playingSong
                    playingSong = if (queueId != null && playingSongData != null) {
                        Pair(queueId, playingSongData)
                    } else {
                        null
                    }
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
                        //　update isActive
                        playingSong = if (queueId != null) {
                            Pair(
                                queueId,
                                playingSongData.copy(playTime = playingSong!!.second.playTime)
                            )
                        } else {
                            null
                        }
                        type = PlayingSongChangeType.CHANGE_ACTIVE
                    } else if (playingSong?.second?.songData?.artwork == null && playingSongData.songData.artwork != null) {
                        // update artwork
                        playingSong = if (queueId != null) {
                            Pair(
                                queueId,
                                playingSongData.copy(playTime = playingSong!!.second.playTime)
                            )
                        } else {
                            null
                        }
                        type = PlayingSongChangeType.DATA_COMPLETE
                    }
                }
            }
            if (type != PlayingSongChangeType.NONE) {
                changedPlayingSong(
                    type,
                    queueId
                )
            }
        }
    }

    private suspend fun changedPlayingSong(
        type: PlayingSongChangeType,
        queueId: Long?,
    ) {
        eventSharedFlow.postEvent(
            EventSharedFlow.SharedEvent.ChangeCurrentSong
        )
        val current = playingSong
        if (type != PlayingSongChangeType.CHANGE_ACTIVE && type != PlayingSongChangeType.NONE) {
            var afterCheckType = AfterCheckType.NONE
            isWaitingMutex.withLock(isWaitingTime) {
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

    private suspend fun waitMetadata(queueId: Long?) {
        if (queueId == null) return
        var isComplete = false
        withContext(Dispatchers.Default) {
            delay(10000)
            val curernt = playingSong
            withContext(Dispatchers.IO) {
                isWaitingMutex.withLock(isWaitingTime) {
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
                "DataComplete \nhasArtwork = ${playingSong?.second?.songData?.artwork != null}"
            )
            saveModel.saveSong(playingSongData)
        }
    }

    suspend fun getCurrentPlayingSong(): PlayingSongData? {
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
        NONE
    }

}