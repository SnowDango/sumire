package com.snowdango.sumire.presenter.report

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.snowdango.sumire.presenter.report.component.PlaySummary
import com.snowdango.sumire.presenter.report.component.RankedArtistCard
import com.snowdango.sumire.presenter.report.component.RankedSongCard
import com.snowdango.sumire.presenter.report.mock.MockData
import com.snowdango.sumire.ui.component.CircleLoading
import com.snowdango.sumire.ui.theme.SumireTheme
import com.snowdango.sumire.ui.viewdata.MonthlyReportViewData
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReportScreen(
    windowSize: WindowSizeClass,
) {
    val viewModel: ReportViewModel = koinViewModel()
    val report = viewModel.monthlyReport.collectAsStateWithLifecycle().value
    val isLandScape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    when {
        report == null -> CircleLoading()

        report.playCount == 0 -> NoPlayReportComponent(
            yearMonthText = report.yearMonthText,
        )

        isLandScape || windowSize.widthSizeClass != WindowWidthSizeClass.Compact -> ReportSplit2Screen(
            report = report,
        )

        else -> ReportCompactScreen(
            report = report,
        )
    }
}

// 縦画面用
@Composable
fun ReportCompactScreen(
    report: MonthlyReportViewData,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
    ) {
        item {
            ReportHeader(report = report)
        }
        item {
            SectionTitle(text = stringResource(R.string.top_songs))
        }
        items(report.topSongs) {
            RankedSongCard(
                rankedSong = it,
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, bottom = 4.dp),
            )
        }
        item {
            SectionTitle(text = stringResource(R.string.top_artists))
        }
        items(report.topArtists) {
            RankedArtistCard(
                rankedArtist = it,
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, bottom = 4.dp),
            )
        }
    }
}

// 横画面とFold用
@Composable
fun ReportSplit2Screen(
    report: MonthlyReportViewData,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item {
                ReportHeader(report = report)
            }
            item {
                SectionTitle(text = stringResource(R.string.top_artists))
            }
            items(report.topArtists) {
                RankedArtistCard(
                    rankedArtist = it,
                    modifier = Modifier
                        .padding(start = 32.dp, end = 32.dp, bottom = 4.dp),
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item {
                SectionTitle(text = stringResource(R.string.top_songs))
            }
            items(report.topSongs) {
                RankedSongCard(
                    rankedSong = it,
                    modifier = Modifier
                        .padding(start = 32.dp, end = 32.dp, bottom = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun ReportHeader(
    report: MonthlyReportViewData,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        SectionTitle(text = stringResource(R.string.report_title, report.yearMonthText))
        PlaySummary(
            playCount = report.playCount,
            songCount = report.songCount,
            artistCount = report.artistCount,
            modifier = Modifier
                .padding(horizontal = 32.dp),
        )
    }
}

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier
            .padding(start = 32.dp, top = 32.dp, end = 32.dp, bottom = 16.dp)
            .fillMaxWidth(),
    )
}

@Composable
fun NoPlayReportComponent(
    yearMonthText: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.no_play_report, yearMonthText),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(group = REPORT_GROUP, name = "NoPlayReport")
@Composable
fun Preview_NoPlayReportComponent() {
    SumireTheme {
        Scaffold {
            NoPlayReportComponent(
                yearMonthText = MockData.report.yearMonthText,
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(group = REPORT_GROUP, name = "ReportCompactScreen")
@Composable
fun Preview_ReportCompactScreen() {
    SumireTheme {
        Scaffold {
            ReportCompactScreen(
                report = MockData.report,
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(
    group = REPORT_GROUP,
    name = "ReportSplit2Screen",
    device = "spec:width=1280dp,height=800dp,dpi=240",
)
@Composable
fun Preview_ReportSplit2Screen() {
    SumireTheme {
        Scaffold {
            ReportSplit2Screen(
                report = MockData.report,
            )
        }
    }
}
