package com.snowdango.sumire.data.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.snowdango.sumire.data.entity.MusicApp

@Entity(AppSongKey.TABLE_NAME)
data class AppSongKey(
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
