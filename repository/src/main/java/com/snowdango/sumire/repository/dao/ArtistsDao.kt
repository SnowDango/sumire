package com.snowdango.sumire.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.snowdango.sumire.data.entity.db.Artists

@Dao
interface ArtistsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(artists: Artists): Long

    @Query("select ${Artists.COLUMN_ID} from ${Artists.TABLE_NAME} where ${Artists.COLUMN_NAME} = :artist limit 1")
    suspend fun getArtist(artist: String): Long?
}
