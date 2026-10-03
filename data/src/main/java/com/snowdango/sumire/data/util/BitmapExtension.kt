package com.snowdango.sumire.data.util

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

private const val MAX_THUMBNAIL_EDGE_PX = 512
private const val JPEG_QUALITY = 85

/**
 * DB やウィジェットの状態に保存するための Base64 文字列にする。
 * 元画像をそのまま PNG にすると 1 行が数百 KB を超えて CursorWindow の上限に当たるため、
 * 長辺を [MAX_THUMBNAIL_EDGE_PX] に抑えた JPEG にする。
 */
fun Bitmap?.toBase64(): String? {
    return this?.let { source ->
        val scaled = source.scaleDown(MAX_THUMBNAIL_EDGE_PX)
        val byteArrayOutputStream = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, byteArrayOutputStream)
        if (scaled !== source) {
            scaled.recycle()
        }
        Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP)
    }
}

private fun Bitmap.scaleDown(maxEdge: Int): Bitmap {
    val longest = maxOf(width, height)
    if (longest <= maxEdge) return this
    val ratio = maxEdge.toFloat() / longest
    return Bitmap.createScaledBitmap(
        this,
        (width * ratio).toInt().coerceAtLeast(1),
        (height * ratio).toInt().coerceAtLeast(1),
        true,
    )
}
