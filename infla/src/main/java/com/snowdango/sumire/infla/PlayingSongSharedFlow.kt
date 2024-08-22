package com.snowdango.sumire.infla

import android.util.Log
import com.snowdango.sumire.data.entity.playing.PlayingSongData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class PlayingSongSharedFlow(private val eventSharedFlow: EventSharedFlow) {

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
                        PlayingSongChangeType.CHANGE
                    } else {
                        PlayingSongChangeType.NONE
                    }
                } else if (playingSongData != null && playingSong?.second?.isActive != playingSongData.isActive) {
                    //　update isActive
                    playingSong = if (queueId != null) {
                        Pair(queueId, playingSongData)
                    } else {
                        null
                    }
                    type = PlayingSongChangeType.CHANGE_ACTIVE
                } else if (playingSong?.second?.songData?.artwork == null && playingSongData?.songData?.artwork != null) {
                    // update artwork
                    playingSong = if (queueId != null) {
                        Pair(queueId, playingSongData)
                    } else {
                        null
                    }
                    type = PlayingSongChangeType.DATA_COMPLETE
                }
            }
            if (type != PlayingSongChangeType.NONE) {
                changedPlayingSong(
                    hasArtwork = playingSong?.second?.songData?.artwork != null,
                    type,
                    queueId
                )
            }
        }
    }

    private suspend fun changedPlayingSong(
        hasArtwork: Boolean,
        type: PlayingSongChangeType,
        queueId: Long?
    ) {
        eventSharedFlow.postEvent(
            EventSharedFlow.SharedEvent.ChangeCurrentSong
        )
        if (type != PlayingSongChangeType.CHANGE_ACTIVE) {
            var afterCheckType = AfterCheckType.NONE
            isWaitingMutex.withLock(isWaitingTime) {
                if (!isWaitingTime) {
                    isWaitingTime = true
                    // check delay
                    afterCheckType = AfterCheckType.WAIT
                } else if (hasArtwork) {
                    // complete
                    isWaitingTime = false
                    afterCheckType = AfterCheckType.COMPLETE
                }
            }
            when (afterCheckType) {
                AfterCheckType.WAIT -> waitMetadata(queueId)
                AfterCheckType.COMPLETE -> {
                    if (type == PlayingSongChangeType.DATA_COMPLETE) {
                        if (isWaitingTime) {
                            metaDataComplete()
                        } else {
                            // update artwork
                        }
                    } else if (type == PlayingSongChangeType.CHANGE) {
                        metaDataComplete()
                    }
                }

                AfterCheckType.NONE -> {}
            }
        }
    }

    private suspend fun waitMetadata(queueId: Long?) {
        if (queueId == null) return
        withContext(Dispatchers.Default) {
            delay(10000)
            withContext(Dispatchers.IO) {
                isWaitingMutex.withLock(isWaitingTime) {
                    if (isWaitingTime) {
                        val current = playingSong
                        if (current?.first == queueId) {
                            isWaitingTime = false
                            metaDataComplete()
                        }
                    }
                }
            }
        }
    }

    private suspend fun metaDataComplete() {
        withContext(Dispatchers.Default) {
            Log.d(
                "CurrentPlayingSong",
                "DataComplete \nhasArtwork = ${playingSong?.second?.songData?.artwork != null}"
            )
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