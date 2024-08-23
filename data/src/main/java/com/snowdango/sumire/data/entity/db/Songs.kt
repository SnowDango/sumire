package com.snowdango.sumire.data.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(Songs.TABLE_NAME)
data class Songs(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(COLUMN_TITLE)
    val title: String,
    @ColumnInfo(COLUMN_ARTIST_ID)
    val artistId: Long,
    @ColumnInfo(COLUMN_ALBUM_ID)
    val albumId: Long,
) {
    companion object {
        const val TABLE_NAME = "songs"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_ARTIST_ID = "artist_id"
        const val COLUMN_ALBUM_ID = "album_id"
    }
}
