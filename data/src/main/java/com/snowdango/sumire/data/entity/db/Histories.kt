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
    val app: MusicApp
) {
    companion object {
        const val TABLE_NAME = "histories"
        const val COLUMN_ID = "id"
        const val COLUMN_SONG_ID = "song_id"
        const val COLUMN_PLAY_TIME = "play_time"
        const val COLUMN_APP = "app"
    }
}
