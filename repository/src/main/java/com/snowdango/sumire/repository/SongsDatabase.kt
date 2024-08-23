package com.snowdango.sumire.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.snowdango.sumire.data.entity.db.Albums
import com.snowdango.sumire.data.entity.db.AppSongKey
import com.snowdango.sumire.data.entity.db.Artists
import com.snowdango.sumire.data.entity.db.Histories
import com.snowdango.sumire.data.entity.db.Songs
import com.snowdango.sumire.data.entity.db.Tasks
import com.snowdango.sumire.repository.dao.AlbumsDao
import com.snowdango.sumire.repository.dao.AppSongKeyDao
import com.snowdango.sumire.repository.dao.ArtistsDao
import com.snowdango.sumire.repository.dao.HistoriesDao
import com.snowdango.sumire.repository.dao.SongsDao
import com.snowdango.sumire.repository.dao.TasksDao
import com.snowdango.sumire.repository.typeconverter.LocalDataTimeConverter

@Database(
    entities = [
        Songs::class,
        Albums::class,
        Artists::class,
        AppSongKey::class,
        Histories::class,
        Tasks::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    LocalDataTimeConverter::class,
)
abstract class SongsDatabase : RoomDatabase() {

    abstract val songsDao: SongsDao
    abstract val tasksDao: TasksDao
    abstract val historiesDao: HistoriesDao
    abstract val artistsDao: ArtistsDao
    abstract val appSongKeyDao: AppSongKeyDao
    abstract val albumsDao: AlbumsDao

    companion object {
        private const val DATABASE_NAME = "song_db"

        @Volatile
        private var INSTANCE: SongsDatabase? = null

        fun getInstance(context: Context): SongsDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SongsDatabase::class.java,
                    DATABASE_NAME
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }

}