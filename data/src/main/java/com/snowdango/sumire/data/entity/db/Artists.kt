package com.snowdango.sumire.data.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(Artists.TABLE_NAME)
data class Artists(
    @PrimaryKey(autoGenerate = true)
    val index: Long = 0,
    @ColumnInfo("name")
    val name: String,
) {
    companion object {
        const val TABLE_NAME = "artists"
    }
}
