package com.snowdango.sumire.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.entity.db.AppSongKey
import com.snowdango.sumire.data.entity.db.relations.SongAppKeys

@Suppress("MaximumLineLength", "MaxLineLength")
@Dao
interface AppSongKeyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(appSongKey: AppSongKey): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(appSongKeys: List<AppSongKey>): List<Long>

    @Query(
        "select ${AppSongKey.COLUMN_SONG_ID} from ${AppSongKey.TABLE_NAME} where ${AppSongKey.COLUMN_KEY} = :key and ${AppSongKey.COLUMN_APP} = :app limit 1"
    )
    suspend fun getSongIdByKey(key: String, app: MusicApp): Long?

    @Query("select * from ${AppSongKey.TABLE_NAME} where ${AppSongKey.COLUMN_SONG_ID} = :songId")
    suspend fun getBySongId(songId: Long): List<AppSongKey>

    @Query(
        "select ${AppSongKey.COLUMN_URL} from ${AppSongKey.TABLE_NAME} where ${AppSongKey.COLUMN_KEY} = :key and ${AppSongKey.COLUMN_APP} = :app limit 1"
    )
    suspend fun getUrlByKey(key: String, app: MusicApp): String?

    @Transaction
    @Query(
        "select * from ${AppSongKey.TABLE_NAME} where ${AppSongKey.COLUMN_KEY} = :key and ${AppSongKey.COLUMN_APP} = :app limit 1"
    )
    suspend fun getAppKeys(key: String, app: MusicApp): SongAppKeys?
}
