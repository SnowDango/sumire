package com.snowdango.sumire.repository.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.snowdango.sumire.data.entity.db.Histories
import com.snowdango.sumire.data.entity.db.Songs
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
    @Query("select ${Histories.TABLE_NAME}.* from ${Histories.TABLE_NAME} inner join ${Songs.TABLE_NAME} on ${Histories.TABLE_NAME}.${Histories.COLUMN_SONG_ID} = ${Songs.TABLE_NAME}.${Songs.COLUMN_ID} where ${Songs.TABLE_NAME}.${Songs.COLUMN_TITLE} like :text order by ${Histories.COLUMN_PLAY_TIME} desc")
    fun getPagingSearchHistorySong(text: String): PagingSource<Int, HistorySong>

    @Transaction
    @Query("select * from ${Histories.TABLE_NAME} order by ${Histories.COLUMN_PLAY_TIME} desc limit :size")
    fun getHistoriesSongRecent(size: Long): Flow<List<HistorySong>>
}
