package com.snowdango.sumire.data.util

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream


fun Bitmap?.toBase64(): String? {
    return this?.let {
        val byteArrayOutputStream = ByteArrayOutputStream()
        it.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP)
    }
}