package com.snowdango.sumire.data.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.snowdango.sumire.data.entity.MusicApp
import kotlinx.datetime.LocalDateTime

@Entity(Histories.TABLE_NAME)
data class Histories(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(COLUMN_SONG_ID)
    val songId: Long,
    @ColumnInfo(COLUMN_PLAY_TIME)
    val playTime: LocalDateTime,
    @ColumnInfo(COLUMN_APP)
    val app: MusicApp,
    // 実際に再生していた時間 (ミリ秒)。一時停止や曲の切り替えのたびに後から足し込むので、
    // まだ一度も書き込まれていない履歴と、記録を始める前 (DB version 3 まで) の履歴は null
    @ColumnInfo(COLUMN_LISTENING_MS)
    val listeningMs: Long? = null,
) {
    companion object {
        const val TABLE_NAME = "histories"
        const val COLUMN_ID = "id"
        const val COLUMN_SONG_ID = "song_id"
        const val COLUMN_PLAY_TIME = "play_time"
        const val COLUMN_APP = "app"
        const val COLUMN_LISTENING_MS = "listening_ms"
    }
}
