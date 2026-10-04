package com.snowdango.sumire.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.snowdango.sumire.data.util.toBitmap
import com.snowdango.sumire.ui.R

/**
 * 曲のサムネイル。URL なら読み込み、それ以外は Base64 の画像として表示し、どちらも無ければ noimage を出す
 */
@Composable
fun SongThumbnail(
    thumbnail: String?,
    isThumbUrl: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (thumbnail != null) {
            if (isThumbUrl) {
                AsyncImage(
                    model = thumbnail,
                    contentDescription = null,
                )
            } else {
                // Base64 のデコードは重いので recomposition のたびにやり直さない
                val bitmap = remember(thumbnail) {
                    thumbnail.toBitmap()
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.noimage),
                        contentDescription = null,
                    )
                }
            }
        } else {
            Image(
                painter = painterResource(id = R.drawable.noimage),
                contentDescription = null,
            )
        }
    }
}
