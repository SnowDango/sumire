package com.snowdango.sumire.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.snowdango.sumire.data.entity.db.Songs

@Dao
interface SongsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(songs: Songs): Long

    @Query("select * from ${Songs.TABLE_NAME} where ${Songs.COLUMN_TITLE} like :searchText limit 6")
    suspend fun getSearchTitle(searchText: String): List<Songs>
}
