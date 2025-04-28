package com.snowdango.sumire.data.entity.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.snowdango.sumire.data.entity.db.Histories
import com.snowdango.sumire.data.entity.db.Songs

data class HistorySong(

    @Embedded val history: Histories,

    @Relation(
        parentColumn = Histories.COLUMN_SONG_ID,
        entityColumn = Songs.COLUMN_ID,
        entity = Songs::class,
    )
    val song: SongMetadata,
)
