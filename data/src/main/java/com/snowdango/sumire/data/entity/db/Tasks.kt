package com.snowdango.sumire.data.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(Tasks.TABLE_NAME)
data class Tasks(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo("media_id")
    val mediaId: String,
    @ColumnInfo("song_id")
    val songId: Long,
) {
    companion object {
        const val TABLE_NAME = "tasks"
    }
}
