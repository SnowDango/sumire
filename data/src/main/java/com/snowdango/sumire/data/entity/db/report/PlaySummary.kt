package com.snowdango.sumire.data.entity.db.report

import androidx.room.ColumnInfo

/**
 * 期間内の再生回数と、再生された曲・アーティストの種類数
 */
data class PlaySummary(
    @ColumnInfo(COLUMN_PLAY_COUNT)
    val playCount: Int,
    @ColumnInfo(COLUMN_SONG_COUNT)
    val songCount: Int,
    @ColumnInfo(COLUMN_ARTIST_COUNT)
    val artistCount: Int,
) {
    companion object {
        const val COLUMN_PLAY_COUNT = "play_count"
        const val COLUMN_SONG_COUNT = "song_count"
        const val COLUMN_ARTIST_COUNT = "artist_count"
    }
}
