package com.snowdango.sumire.data.entity.playing

import kotlinx.datetime.LocalDateTime

data class PlayingSongData(
    val songData: SongData,
    val isActive: Boolean,
    val playTime: LocalDateTime,
)
