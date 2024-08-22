package com.snowdango.sumire.data.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(Histories.TABLE_NAME)
data class Histories(
    @PrimaryKey(autoGenerate = true)
    val index: Long = 0,
    @ColumnInfo("song_id")
    val songId: Long,
    @ColumnInfo("play_time")
    val playTime: LocalDateTime,
) {
    companion object {
        const val TABLE_NAME = "histories"
    }
}
