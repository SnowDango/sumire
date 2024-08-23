package com.snowdango.sumire.data.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(Albums.TABLE_NAME)
data class Albums(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(COLUMN_NAME)
    val name: String,
    @ColumnInfo(COLUMN_ARTIST_ID)
    val artistId: Long,
) {
    companion object {
        const val TABLE_NAME = "albums"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_ARTIST_ID = "artist_id"
    }
}
