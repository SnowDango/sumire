package com.snowdango.sumire.data.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(Albums.TABLE_NAME)
data class Albums(
    @PrimaryKey(autoGenerate = true)
    val index: Long = 0,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("artist_id")
    val artistId: Long,
) {
    companion object {
        const val TABLE_NAME = "albums"
    }
}
