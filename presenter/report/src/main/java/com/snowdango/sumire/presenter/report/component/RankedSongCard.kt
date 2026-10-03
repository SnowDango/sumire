package com.snowdango.sumire.presenter.report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.snowdango.sumire.ui.component.SongThumbnail
import com.snowdango.sumire.ui.theme.SumireTheme
import com.snowdango.sumire.ui.viewdata.RankedSongViewData

@Composable
fun RankedSongCard(
    rankedSong: RankedSongViewData,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxWidth()
            .padding(start = 12.dp, top = 10.dp, bottom = 10.dp, end = 16.dp)
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RankText(rank = rankedSong.rank)
        SongThumbnail(
            thumbnail = rankedSong.thumbnail,
            isThumbUrl = rankedSong.isThumbUrl,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(60.dp),
        )
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = rankedSong.title,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            Text(
                text = rankedSong.albumName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            Text(
                text = rankedSong.artistName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        PlayCountText(
            playCount = rankedSong.playCount,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Preview(group = REPORT_GROUP, name = "RankedSongCard")
@Composable
fun Preview_RankedSongCard() {
    SumireTheme {
        RankedSongCard(
            rankedSong = MockData.report.topSongs.first(),
        )
    }
}
