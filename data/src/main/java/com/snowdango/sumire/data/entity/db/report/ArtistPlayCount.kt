package com.snowdango.sumire.data.entity.db.report

import androidx.room.ColumnInfo

/**
 * 期間内のアーティストごとの再生回数
 */
data class ArtistPlayCount(
    @ColumnInfo(COLUMN_ARTIST_ID)
    val artistId: Long,
    @ColumnInfo(COLUMN_NAME)
    val name: String,
    @ColumnInfo(COLUMN_PLAY_COUNT)
    val playCount: Int,
) {
    companion object {
        const val COLUMN_ARTIST_ID = "artist_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PLAY_COUNT = "play_count"
    }
}
