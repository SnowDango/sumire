package com.snowdango.sumire.data.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(Tasks.TABLE_NAME)
data class Tasks(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(COLUMN_MEDIA_ID)
    val mediaId: String,
    @ColumnInfo(COLUMN_SONG_ID)
    val songId: Long,
) {
    companion object {
        const val TABLE_NAME = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_MEDIA_ID = "media_id"
        const val COLUMN_SONG_ID = "song_id"
    }
}
