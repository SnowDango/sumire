package com.snowdango.sumire.data.entity.db.report

import androidx.room.ColumnInfo

/**
 * 期間内の曲ごとの再生回数
 */
data class SongPlayCount(
    @ColumnInfo(COLUMN_SONG_ID)
    val songId: Long,
    @ColumnInfo(COLUMN_TITLE)
    val title: String,
    @ColumnInfo(COLUMN_ARTIST_NAME)
    val artistName: String,
    @ColumnInfo(COLUMN_ALBUM_NAME)
    val albumName: String,
    @ColumnInfo(COLUMN_THUMBNAIL)
    val thumbnail: String?,
    @ColumnInfo(COLUMN_IS_THUMB_URL)
    val isThumbUrl: Boolean,
    @ColumnInfo(COLUMN_PLAY_COUNT)
    val playCount: Int,
) {
    companion object {
        const val COLUMN_SONG_ID = "song_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_ARTIST_NAME = "artist_name"
        const val COLUMN_ALBUM_NAME = "album_name"
        const val COLUMN_THUMBNAIL = "thumbnail"
        const val COLUMN_IS_THUMB_URL = "is_thumb_url"
        const val COLUMN_PLAY_COUNT = "play_count"
    }
}
