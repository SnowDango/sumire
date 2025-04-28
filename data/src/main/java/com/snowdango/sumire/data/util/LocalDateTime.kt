package com.snowdango.sumire.data.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant

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

fun LocalDateTime.toLastDateTimeString(
    currentDateTime: LocalDateTime
): String {
    val duration = currentDateTime.toInstant(TimeZone.currentSystemDefault()) -
        this.toInstant(TimeZone.currentSystemDefault())
    return if (duration.inWholeDays > 0) {
        return "${duration.inWholeDays}d ago"
    } else if (duration.inWholeHours > 0) {
        return "${duration.inWholeHours}h ago"
    } else if (duration.inWholeMinutes > 0) {
        return "${duration.inWholeMinutes}m ago"
    } else if (duration.inWholeSeconds > 0) {
        return "${duration.inWholeSeconds}s ago"
    } else {
        return "now"
    }
}

enum class LocalDateTimeFormatType(
    val pattern: String,
) {
    ONLY_DATE("yyyy/MM/dd"),
    FULL_DATE_TIME("yyyy/MM/dd-HH:mm:ss"),
    ONLY_TIME("HH:mm:ss"),
}
