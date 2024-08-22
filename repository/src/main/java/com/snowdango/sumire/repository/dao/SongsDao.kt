package com.snowdango.sumire.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.snowdango.sumire.data.entity.db.Songs

@Dao
interface SongsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(songs: Songs): Long

    @Query("select exists ( select * from ${Songs.TABLE_NAME} where ${Songs.COLUMN_ID} = :index limit 1)")
    fun hasSong(index: Long): Boolean

}