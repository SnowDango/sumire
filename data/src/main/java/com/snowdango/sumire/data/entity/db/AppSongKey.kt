package com.snowdango.sumire.data.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.snowdango.sumire.data.entity.MusicApp

@Entity(AppSongKey.TABLE_NAME)
data class AppSongKey(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(COLUMN_SONG_ID)
    val songId: Long,
    @ColumnInfo(COLUMN_APP)
    val app: MusicApp,
    @ColumnInfo(COLUMN_KEY)
    val key: String,
) {
    companion object {
        const val TABLE_NAME = "appSongKey"
        const val COLUMN_ID = "id"
        const val COLUMN_SONG_ID = "song_id"
        const val COLUMN_APP = "app"
        const val COLUMN_KEY = "key"
    }
}
