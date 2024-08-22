package com.snowdango.sumire.data.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.snowdango.sumire.data.entity.MusicApp

@Entity(AppSongKey.TABLE_NAME)
data class AppSongKey(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo("song_id")
    val songId: Long,
    @ColumnInfo("app")
    val app: MusicApp,
    @ColumnInfo("key")
    val key: String,
) {
    companion object {
        const val TABLE_NAME = "appSongKey"
    }
}
