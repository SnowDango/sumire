package com.snowdango.sumire.data.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(Artists.TABLE_NAME)
data class Artists(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(COLUMN_NAME)
    val name: String,
) {
    companion object {
        const val TABLE_NAME = "artists"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
    }
}
