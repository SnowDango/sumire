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
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun PlaySummary(
    playCount: Int,
    songCount: Int,
    artistCount: Int,
    listeningTime: Duration,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // 「12h 34m」は他のタイルより幅を取るので、1 行使って上に置く
        PlaySummaryItem(
            value = listeningTimeText(listeningTime),
            label = stringResource(R.string.summary_listening_time),
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PlaySummaryItem(
                value = playCount.toString(),
                label = stringResource(R.string.summary_plays),
                modifier = Modifier.weight(1f),
            )
            PlaySummaryItem(
                value = songCount.toString(),
                label = stringResource(R.string.summary_songs),
                modifier = Modifier.weight(1f),
            )
            PlaySummaryItem(
                value = artistCount.toString(),
                label = stringResource(R.string.summary_artists),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// 月の合計なので秒は出さず、1 分未満は切り捨てる
@Composable
private fun listeningTimeText(listeningTime: Duration): String {
    return listeningTime.toComponents { wholeHours, minutesOfHour, _, _ ->
        if (wholeHours > 0) {
            stringResource(R.string.listening_time_hours_minutes, wholeHours, minutesOfHour)
        } else {
            stringResource(R.string.listening_time_minutes, minutesOfHour)
        }
    }
}

@Composable
private fun PlaySummaryItem(
    value: String,
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
            text = value,
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
                listeningTime = 8.hours + 32.minutes,
            )
        }
    }
}
