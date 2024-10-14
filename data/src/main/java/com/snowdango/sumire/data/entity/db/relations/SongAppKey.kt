package com.snowdango.sumire.data.entity.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.snowdango.sumire.data.entity.db.AppSongKey
import com.snowdango.sumire.data.entity.db.Songs

data class SongAppKeys(
    @Embedded val targetKey: AppSongKey,

    @Relation(
        parentColumn = AppSongKey.COLUMN_SONG_ID,
        entityColumn = Songs.COLUMN_ID,
        entity = Songs::class,
    )
    val song: Songs,

    @Relation(
        parentColumn = Songs.COLUMN_ID,
        entityColumn = AppSongKey.COLUMN_SONG_ID,
        entity = AppSongKey::class,
    )
    val appSongKeys: List<AppSongKey>


)
