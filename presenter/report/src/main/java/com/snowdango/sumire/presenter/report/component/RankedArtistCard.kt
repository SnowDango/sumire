package com.snowdango.sumire.presenter.report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.snowdango.sumire.presenter.report.REPORT_GROUP
import com.snowdango.sumire.presenter.report.mock.MockData
import com.snowdango.sumire.ui.theme.SumireTheme
import com.snowdango.sumire.ui.viewdata.RankedArtistViewData

@Composable
fun RankedArtistCard(
    rankedArtist: RankedArtistViewData,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxWidth()
            .padding(start = 12.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RankText(rank = rankedArtist.rank)
        Text(
            text = rankedArtist.name,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
        )
        PlayCountText(
            playCount = rankedArtist.playCount,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Preview(group = REPORT_GROUP, name = "RankedArtistCard")
@Composable
fun Preview_RankedArtistCard() {
    SumireTheme {
        RankedArtistCard(
            rankedArtist = MockData.report.topArtists.first(),
        )
    }
}
