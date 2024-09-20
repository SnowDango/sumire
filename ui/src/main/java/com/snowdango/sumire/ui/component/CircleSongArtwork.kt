package com.snowdango.sumire.ui.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.drawable.toBitmap
import com.snowdango.sumire.ui.R
import com.snowdango.sumire.ui.UTIL_GROUP
import com.snowdango.sumire.ui.theme.SumireTheme

@Composable
fun CircleSongArtwork(
    modifier: Modifier,
    bitmap: Bitmap?,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(group = UTIL_GROUP, name = "CircleSongArtwork")
@Composable
fun PreviewCircleSongArtwork() {
    SumireTheme {
        CircleSongArtwork(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            bitmap = LocalContext.current.resources.getDrawable(R.drawable.apple_music).toBitmap(),
        )
    }
}