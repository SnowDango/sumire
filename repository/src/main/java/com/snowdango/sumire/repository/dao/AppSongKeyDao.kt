package com.snowdango.sumire.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.entity.db.AppSongKey


@Dao
interface AppSongKeyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(appSongKey: AppSongKey): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(appSongKeys: List<AppSongKey>): List<Long>

    @Query("select ${AppSongKey.COLUMN_SONG_ID} from ${AppSongKey.TABLE_NAME} where ${AppSongKey.COLUMN_KEY} = :key and ${AppSongKey.COLUMN_APP} = :app limit 1")
    suspend fun getSongIdByKey(key: String, app: MusicApp): Long?

    @Query("select * from ${AppSongKey.TABLE_NAME} where ${AppSongKey.COLUMN_SONG_ID} = :songId")
    suspend fun getBySongId(songId: Long): List<AppSongKey>

}