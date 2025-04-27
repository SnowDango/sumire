package com.snowdango.sumire.data.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

@OptIn(FormatStringsInDatetimeFormats::class)
fun LocalDateTime.toFormatString(
    type: LocalDateTimeFormatType,
): String {
    return this.format(
        format = LocalDateTime.Format {
            byUnicodePattern(type.pattern)
        },
    )
}

enum class LocalDateTimeFormatType(
    val pattern: String,
) {
    ONLY_DATE("yyyy/MM/dd"),
    FULL_DATE_TIME("yyyy/MM/dd-HH:mm:ss"),
    ONLY_TIME("HH:mm:ss"),
}
