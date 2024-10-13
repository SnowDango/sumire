package com.snowdango.sumire.repository.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.snowdango.sumire.data.entity.db.Histories
import com.snowdango.sumire.data.entity.db.relations.HistorySong
import kotlinx.coroutines.flow.Flow


@Dao
interface HistoriesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(histories: Histories): Long

    @Query("select * from ${Histories.TABLE_NAME} order by ${Histories.COLUMN_PLAY_TIME} desc limit :from,:size")
    suspend fun getHistories(from: Long, size: Long): List<Histories>

    @Transaction
    @Query("select * from ${Histories.TABLE_NAME} order by ${Histories.COLUMN_PLAY_TIME} desc")
    fun getPagingHistorySongs(): PagingSource<Int, HistorySong>

    @Transaction
    @Query("select * from ${Histories.TABLE_NAME} order by ${Histories.COLUMN_PLAY_TIME} desc limit :size")

    fun getHistoriesSongRecent(size: Long): Flow<List<HistorySong>>

}