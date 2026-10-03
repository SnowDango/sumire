package com.snowdango.sumire.data.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

fun String?.toBitmap(): Bitmap? {
    if (this.isNullOrBlank()) return null
    return try {
        val decodedString = Base64.decode(this, Base64.NO_WRAP)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } catch (e: IllegalArgumentException) {
        // 壊れた Base64 文字列は画像なしとして扱う
        null
    }
}

/**
 * SQL の LIKE に埋め込む文字列をエスケープする。
 * クエリ側で `escape '\\'` を指定していることが前提。
 */
fun String.escapeLike(): String =
    replace("\\", "\\\\")
        .replace("%", "\\%")
        .replace("_", "\\_")
