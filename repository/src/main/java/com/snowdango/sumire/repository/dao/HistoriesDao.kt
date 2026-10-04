package com.snowdango.sumire.repository.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.snowdango.sumire.data.entity.db.Albums
import com.snowdango.sumire.data.entity.db.Artists
import com.snowdango.sumire.data.entity.db.Histories
import com.snowdango.sumire.data.entity.db.Songs
import com.snowdango.sumire.data.entity.db.relations.HistorySong
import com.snowdango.sumire.data.entity.db.report.ArtistPlayCount
import com.snowdango.sumire.data.entity.db.report.PlaySummary
import com.snowdango.sumire.data.entity.db.report.SongPlayCount
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

// 集計クエリで共通に使う結合と期間の条件。期間は startInclusive 以上 endExclusive 未満
private const val HISTORIES_JOIN_SONGS =
    "${Histories.TABLE_NAME} inner join ${Songs.TABLE_NAME} " +
        "on ${Histories.TABLE_NAME}.${Histories.COLUMN_SONG_ID} = ${Songs.TABLE_NAME}.${Songs.COLUMN_ID}"
private const val JOIN_ARTISTS =
    "inner join ${Artists.TABLE_NAME} " +
        "on ${Songs.TABLE_NAME}.${Songs.COLUMN_ARTIST_ID} = ${Artists.TABLE_NAME}.${Artists.COLUMN_ID}"
private const val JOIN_ALBUMS =
    "inner join ${Albums.TABLE_NAME} " +
        "on ${Songs.TABLE_NAME}.${Songs.COLUMN_ALBUM_ID} = ${Albums.TABLE_NAME}.${Albums.COLUMN_ID}"
private const val PLAY_TIME_IN_RANGE =
    "${Histories.TABLE_NAME}.${Histories.COLUMN_PLAY_TIME} >= :startInclusive " +
        "and ${Histories.TABLE_NAME}.${Histories.COLUMN_PLAY_TIME} < :endExclusive"

// ランキングで再生回数が同じときは、最後に再生したのが新しいほうを上にする
private const val LAST_PLAY_TIME = "max(${Histories.TABLE_NAME}.${Histories.COLUMN_PLAY_TIME})"

@Suppress("MaxLineLength")
@Dao
interface HistoriesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(histories: Histories): Long

    @Transaction
    @Query("select * from ${Histories.TABLE_NAME} order by ${Histories.COLUMN_PLAY_TIME} desc")
    fun getPagingHistorySongs(): PagingSource<Int, HistorySong>

    @Transaction
    @Query(
        "select ${Histories.TABLE_NAME}.* from ${Histories.TABLE_NAME} inner join ${Songs.TABLE_NAME} on ${Histories.TABLE_NAME}.${Histories.COLUMN_SONG_ID} = ${Songs.TABLE_NAME}.${Songs.COLUMN_ID} where ${Songs.TABLE_NAME}.${Songs.COLUMN_TITLE} like :text escape '\\' order by ${Histories.COLUMN_PLAY_TIME} desc"
    )
    fun getPagingSearchHistorySong(text: String): PagingSource<Int, HistorySong>

    @Transaction
    @Query("select * from ${Histories.TABLE_NAME} order by ${Histories.COLUMN_PLAY_TIME} desc limit :size")
    fun getHistoriesSongRecent(size: Long): Flow<List<HistorySong>>

    @Query(
        "select count(*) as ${PlaySummary.COLUMN_PLAY_COUNT}, " +
            "count(distinct ${Histories.TABLE_NAME}.${Histories.COLUMN_SONG_ID}) as ${PlaySummary.COLUMN_SONG_COUNT}, " +
            "count(distinct ${Songs.TABLE_NAME}.${Songs.COLUMN_ARTIST_ID}) as ${PlaySummary.COLUMN_ARTIST_COUNT} " +
            "from $HISTORIES_JOIN_SONGS where $PLAY_TIME_IN_RANGE"
    )
    fun getPlaySummary(startInclusive: LocalDateTime, endExclusive: LocalDateTime): Flow<PlaySummary>

    @Query(
        "select ${Songs.TABLE_NAME}.${Songs.COLUMN_ID} as ${SongPlayCount.COLUMN_SONG_ID}, " +
            "${Songs.TABLE_NAME}.${Songs.COLUMN_TITLE} as ${SongPlayCount.COLUMN_TITLE}, " +
            "${Artists.TABLE_NAME}.${Artists.COLUMN_NAME} as ${SongPlayCount.COLUMN_ARTIST_NAME}, " +
            "${Albums.TABLE_NAME}.${Albums.COLUMN_NAME} as ${SongPlayCount.COLUMN_ALBUM_NAME}, " +
            "${Albums.TABLE_NAME}.${Albums.COLUMN_THUMBNAIL} as ${SongPlayCount.COLUMN_THUMBNAIL}, " +
            "${Albums.TABLE_NAME}.${Albums.COLUMN_IS_THUMB_URL} as ${SongPlayCount.COLUMN_IS_THUMB_URL}, " +
            "count(*) as ${SongPlayCount.COLUMN_PLAY_COUNT} " +
            "from $HISTORIES_JOIN_SONGS $JOIN_ARTISTS $JOIN_ALBUMS where $PLAY_TIME_IN_RANGE " +
            "group by ${Songs.TABLE_NAME}.${Songs.COLUMN_ID} " +
            "order by ${SongPlayCount.COLUMN_PLAY_COUNT} desc, $LAST_PLAY_TIME desc limit :limit"
    )
    fun getTopSongs(startInclusive: LocalDateTime, endExclusive: LocalDateTime, limit: Int): Flow<List<SongPlayCount>>

    @Query(
        "select ${Artists.TABLE_NAME}.${Artists.COLUMN_ID} as ${ArtistPlayCount.COLUMN_ARTIST_ID}, " +
            "${Artists.TABLE_NAME}.${Artists.COLUMN_NAME} as ${ArtistPlayCount.COLUMN_NAME}, " +
            "count(*) as ${ArtistPlayCount.COLUMN_PLAY_COUNT} " +
            "from $HISTORIES_JOIN_SONGS $JOIN_ARTISTS where $PLAY_TIME_IN_RANGE " +
            "group by ${Artists.TABLE_NAME}.${Artists.COLUMN_ID} " +
            "order by ${ArtistPlayCount.COLUMN_PLAY_COUNT} desc, $LAST_PLAY_TIME desc limit :limit"
    )
    fun getTopArtists(startInclusive: LocalDateTime, endExclusive: LocalDateTime, limit: Int): Flow<List<ArtistPlayCount>>
}
