package com.snowdango.sumire.data.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64


fun String?.toBitmap(): Bitmap? {
    return this?.let {
        if (it.isBlank()) return null
        val decodedString = Base64.decode(it, Base64.NO_WRAP)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}