package com.snowdango.sumire.data.entity.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.snowdango.sumire.data.entity.db.Albums
import com.snowdango.sumire.data.entity.db.Artists
import com.snowdango.sumire.data.entity.db.Songs

data class SongMetadata(
    @Embedded val songs: Songs,

    @Relation(
        parentColumn = Songs.COLUMN_ARTIST_ID,
        entityColumn = Artists.COLUMN_ID,
        entity = Artists::class,
    )
    val artists: Artists,

    @Relation(
        parentColumn = Songs.COLUMN_ALBUM_ID,
        entityColumn = Albums.COLUMN_ID,
        entity = Albums::class,
    )
    val albums: Albums
)
