package com.snowdango.sumire.model

import com.snowdango.sumire.data.entity.db.report.ArtistPlayCount
import com.snowdango.sumire.data.entity.db.report.PlaySummary
import com.snowdango.sumire.data.entity.db.report.SongPlayCount
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class GetReportModelTest {

    @Test
    fun toLocalDateTimeRange_coversWholeMonth() {
        val range = YearMonth(2026, 10).toLocalDateTimeRange()

        assertEquals(LocalDateTime(2026, 10, 1, 0, 0), range.start)
        assertEquals(LocalDateTime(2026, 11, 1, 0, 0), range.endExclusive)
        assertTrue(LocalDateTime(2026, 10, 31, 23, 59, 59) in range)
        assertFalse(LocalDateTime(2026, 11, 1, 0, 0) in range)
    }

    @Test
    fun toLocalDateTimeRange_decemberEndsAtNextYear() {
        val range = YearMonth(2026, 12).toLocalDateTimeRange()

        assertEquals(LocalDateTime(2026, 12, 1, 0, 0), range.start)
        assertEquals(LocalDateTime(2027, 1, 1, 0, 0), range.endExclusive)
    }

    @Test
    fun convertToMonthlyReportViewData_ranksInGivenOrder() {
        val viewData = convertToMonthlyReportViewData(
            yearMonthText = "2026/10",
            summary = PlaySummary(
                playCount = 12,
                songCount = 3,
                artistCount = 2,
                listeningMs = (1.hours + 23.minutes + 450.milliseconds).inWholeMilliseconds,
            ),
            topSongs = listOf(
                songPlayCount(songId = 3, title = "most", playCount = 7),
                songPlayCount(songId = 1, title = "second", playCount = 3),
                songPlayCount(songId = 2, title = "third", playCount = 2),
            ),
            topArtists = listOf(
                ArtistPlayCount(artistId = 2, name = "artist2", playCount = 9),
                ArtistPlayCount(artistId = 1, name = "artist1", playCount = 3),
            ),
        )

        assertEquals("2026/10", viewData.yearMonthText)
        assertEquals(12, viewData.playCount)
        assertEquals(3, viewData.songCount)
        assertEquals(2, viewData.artistCount)
        assertEquals(1.hours + 23.minutes + 450.milliseconds, viewData.listeningTime)
        assertEquals(listOf(1, 2, 3), viewData.topSongs.map { it.rank })
        assertEquals(listOf("most", "second", "third"), viewData.topSongs.map { it.title })
        assertEquals(listOf(7, 3, 2), viewData.topSongs.map { it.playCount })
        assertEquals(listOf(1, 2), viewData.topArtists.map { it.rank })
        assertEquals(listOf("artist2", "artist1"), viewData.topArtists.map { it.name })
        assertEquals(listOf(9, 3), viewData.topArtists.map { it.playCount })
    }

    @Test
    fun convertToMonthlyReportViewData_noPlays() {
        val viewData = convertToMonthlyReportViewData(
            yearMonthText = "2026/10",
            summary = PlaySummary(playCount = 0, songCount = 0, artistCount = 0, listeningMs = 0),
            topSongs = listOf(),
            topArtists = listOf(),
        )

        assertEquals(0, viewData.playCount)
        assertEquals(Duration.ZERO, viewData.listeningTime)
        assertTrue(viewData.topSongs.isEmpty())
        assertTrue(viewData.topArtists.isEmpty())
    }

    private fun songPlayCount(songId: Long, title: String, playCount: Int) = SongPlayCount(
        songId = songId,
        title = title,
        artistName = "artist",
        albumName = "album",
        thumbnail = null,
        isThumbUrl = false,
        playCount = playCount,
    )
}
