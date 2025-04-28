package com.snowdango.sumire.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.snowdango.sumire.data.entity.db.Albums

@Dao
interface AlbumsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(albums: Albums): Long

    @Query(
        "select ${Albums.COLUMN_ID} from ${Albums.TABLE_NAME} where ${Albums.COLUMN_NAME} = :name and ${Albums.COLUMN_ARTIST_ID} = :artistId limit 1"
    )
    suspend fun getIdByNameAndArtistId(name: String, artistId: Long): Long?
}
