package com.snowdango.sumire.data.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

@OptIn(FormatStringsInDatetimeFormats::class)
fun LocalDateTime.toFormatDateTime(): String {
    return this.format(
        format = LocalDateTime.Format {
            byUnicodePattern("yyyy/MM/DD-HH:mm:ss")
        }
    )
}