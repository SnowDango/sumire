package com.snowdango.sumire.presenter.report

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.snowdango.sumire.presenter.report.component.Preview_PlaySummary
import com.snowdango.sumire.presenter.report.component.Preview_PlaySummaryWithUnmeasuredPlays
import com.snowdango.sumire.presenter.report.component.Preview_RankedArtistCard
import com.snowdango.sumire.presenter.report.component.Preview_RankedSongCard

// Compose Preview Screenshot Testing は screenshotTest source set にある @PreviewTest の
// Preview だけを撮影する。中身は main の Preview を呼び出し、@Preview のパラメータは
// main 側と揃えておくこと。

@PreviewTest
@Preview(group = REPORT_GROUP, name = "PlaySummary")
@Composable
fun PlaySummaryPreviewTest() {
    Preview_PlaySummary()
}

@PreviewTest
@Preview(group = REPORT_GROUP, name = "PlaySummaryWithUnmeasuredPlays")
@Composable
fun PlaySummaryWithUnmeasuredPlaysPreviewTest() {
    Preview_PlaySummaryWithUnmeasuredPlays()
}

@PreviewTest
@Preview(group = REPORT_GROUP, name = "RankedSongCard")
@Composable
fun RankedSongCardPreviewTest() {
    Preview_RankedSongCard()
}

@PreviewTest
@Preview(group = REPORT_GROUP, name = "RankedArtistCard")
@Composable
fun RankedArtistCardPreviewTest() {
    Preview_RankedArtistCard()
}

@PreviewTest
@Preview(group = REPORT_GROUP, name = "NoPlayReport")
@Composable
fun NoPlayReportPreviewTest() {
    Preview_NoPlayReportComponent()
}

@PreviewTest
@Preview(group = REPORT_GROUP, name = "ReportCompactScreen")
@Composable
fun ReportCompactScreenPreviewTest() {
    Preview_ReportCompactScreen()
}

@PreviewTest
@Preview(
    group = REPORT_GROUP,
    name = "ReportSplit2Screen",
    device = "spec:width=1280dp,height=800dp,dpi=240",
)
@Composable
fun ReportSplit2ScreenPreviewTest() {
    Preview_ReportSplit2Screen()
}
