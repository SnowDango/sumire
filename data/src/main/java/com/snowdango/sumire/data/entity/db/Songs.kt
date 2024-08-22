package com.snowdango.sumire.data.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(Songs.TABLE_NAME)
data class Songs(
    @PrimaryKey(autoGenerate = true)
    val index: Long = 0,
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("artist_id")
    val artistId: Long,
    @ColumnInfo("album_id")
    val albumId: Long,
    @ColumnInfo("thumbnail")
    val thumbnail: String?,
    @ColumnInfo("is_thumb_url")
    val isThumbUrl: Boolean,
) {
    companion object {
        const val TABLE_NAME = "songs"
    }
}
