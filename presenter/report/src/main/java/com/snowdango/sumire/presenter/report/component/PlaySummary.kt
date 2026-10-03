package com.snowdango.sumire.presenter.report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.snowdango.sumire.presenter.report.R
import com.snowdango.sumire.presenter.report.REPORT_GROUP
import com.snowdango.sumire.ui.theme.SumireTheme

@Composable
fun PlaySummary(
    playCount: Int,
    songCount: Int,
    artistCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PlaySummaryItem(
            value = playCount,
            label = stringResource(R.string.summary_plays),
            modifier = Modifier.weight(1f),
        )
        PlaySummaryItem(
            value = songCount,
            label = stringResource(R.string.summary_songs),
            modifier = Modifier.weight(1f),
        )
        PlaySummaryItem(
            value = artistCount,
            label = stringResource(R.string.summary_artists),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PlaySummaryItem(
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
        )
    }
}

@Preview(group = REPORT_GROUP, name = "PlaySummary")
@Composable
fun Preview_PlaySummary() {
    SumireTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
        ) {
            PlaySummary(
                playCount = 128,
                songCount = 64,
                artistCount = 12,
            )
        }
    }
}
