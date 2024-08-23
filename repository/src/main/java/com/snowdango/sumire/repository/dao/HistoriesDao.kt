package com.snowdango.sumire.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.snowdango.sumire.data.entity.db.Histories


@Dao
interface HistoriesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(histories: Histories): Long

    @Query("select * from ${Histories.TABLE_NAME} order by ${Histories.COLUMN_PLAY_TIME} desc limit :from,:size")
    suspend fun getHistories(from: Long, size: Long): List<Histories>

}