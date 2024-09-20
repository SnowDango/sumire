package com.snowdango.sumire.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.snowdango.sumire.data.entity.MusicApp
import com.snowdango.sumire.data.util.toBitmap
import com.snowdango.sumire.ui.R
import com.snowdango.sumire.ui.UTIL_GROUP
import com.snowdango.sumire.ui.theme.SumireTheme
import com.snowdango.sumire.ui.viewdata.SongCardViewData

@Composable
fun ListSongCard(
    songCardViewData: SongCardViewData,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(start = 20.dp, top = 10.dp, bottom = 10.dp, end = 12.dp)
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .size(60.dp)
            ) {
                if (songCardViewData.thumbnail != null) {
                    if (songCardViewData.isThumbUrl) {
                        AsyncImage(
                            model = songCardViewData.thumbnail,
                            contentDescription = null
                        )
                    } else {
                        Image(
                            bitmap = songCardViewData.thumbnail.toBitmap()!!.asImageBitmap(),
                            contentDescription = null
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.noimage),
                        contentDescription = null
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = songCardViewData.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                Text(
                    text = songCardViewData.albumName,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                Text(
                    text = songCardViewData.artistName,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    MusicAppImage(
                        app = songCardViewData.app,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        text = songCardViewData.playTimeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Preview(group = UTIL_GROUP, name = "ListSongCard")
@Composable
fun PreviewListSongCard() {
    SumireTheme {
        ListSongCard(
            songCardViewData = SongCardViewData(
                title = "Title",
                artistName = "artistName",
                albumName = "albumName",
                thumbnail = null,
                isThumbUrl = false,
                playTimeText = "01:51:22",
                app = MusicApp.APPLE_MUSIC
            )
        )
    }
}