package com.snowdango.sumire.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.snowdango.sumire.data.entity.db.Songs

@Dao
interface SongsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(songs: Songs): Long

}