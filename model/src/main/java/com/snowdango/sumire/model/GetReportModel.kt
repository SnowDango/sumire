package com.snowdango.sumire.model

import com.snowdango.sumire.data.entity.db.report.ArtistPlayCount
import com.snowdango.sumire.data.entity.db.report.PlaySummary
import com.snowdango.sumire.data.entity.db.report.SongPlayCount
import com.snowdango.sumire.data.util.LocalDateTimeFormatType
import com.snowdango.sumire.data.util.toFormatString
import com.snowdango.sumire.ui.viewdata.MonthlyReportViewData
import com.snowdango.sumire.ui.viewdata.RankedArtistViewData
import com.snowdango.sumire.ui.viewdata.RankedSongViewData
import com.snowdango.sumire.usecase.db.HistoriesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.atTime
import kotlinx.datetime.plusMonth
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.yearMonth
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

class GetReportModel : KoinComponent {

    private val historiesUseCase: HistoriesUseCase by inject()

    /**
     * 当月のレポート。collect するたびに当月を求め直すので、
     * 画面を開いたまま月をまたいでも前の月のレポートを出し続けない
     */
    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
    fun getCurrentMonthReportFlow(): Flow<MonthlyReportViewData> {
        return flow {
            emit(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.yearMonth)
        }.flatMapLatest { yearMonth ->
            getMonthlyReportFlow(yearMonth)
        }
    }

    fun getMonthlyReportFlow(yearMonth: YearMonth): Flow<MonthlyReportViewData> {
        val range = yearMonth.toLocalDateTimeRange()
        return combine(
            historiesUseCase.getPlaySummary(range.start, range.endExclusive),
            historiesUseCase.getTopSongs(range.start, range.endExclusive, TOP_SONGS_LIMIT),
            historiesUseCase.getTopArtists(range.start, range.endExclusive, TOP_ARTISTS_LIMIT),
        ) { summary, topSongs, topArtists ->
            convertToMonthlyReportViewData(
                yearMonthText = range.start.toFormatString(LocalDateTimeFormatType.YEAR_MONTH),
                summary = summary,
                topSongs = topSongs,
                topArtists = topArtists,
            )
        }
    }

    companion object {
        const val TOP_SONGS_LIMIT = 5
        const val TOP_ARTISTS_LIMIT = 3
    }
}

/**
 * 月初 0 時から翌月初 0 時まで(翌月初は含まない)
 */
internal fun YearMonth.toLocalDateTimeRange(): OpenEndRange<LocalDateTime> {
    return firstDay.atTime(0, 0)..<plusMonth().firstDay.atTime(0, 0)
}

internal fun convertToMonthlyReportViewData(
    yearMonthText: String,
    summary: PlaySummary,
    topSongs: List<SongPlayCount>,
    topArtists: List<ArtistPlayCount>,
): MonthlyReportViewData {
    return MonthlyReportViewData(
        yearMonthText = yearMonthText,
        playCount = summary.playCount,
        songCount = summary.songCount,
        artistCount = summary.artistCount,
        listeningTime = summary.listeningMs.milliseconds,
        unmeasuredPlayCount = summary.unmeasuredPlayCount,
        topSongs = topSongs.mapIndexed { index, song ->
            RankedSongViewData(
                rank = index + 1,
                title = song.title,
                artistName = song.artistName,
                albumName = song.albumName,
                thumbnail = song.thumbnail,
                isThumbUrl = song.isThumbUrl,
                playCount = song.playCount,
            )
        },
        topArtists = topArtists.mapIndexed { index, artist ->
            RankedArtistViewData(
                rank = index + 1,
                name = artist.name,
                playCount = artist.playCount,
            )
        },
    )
}
