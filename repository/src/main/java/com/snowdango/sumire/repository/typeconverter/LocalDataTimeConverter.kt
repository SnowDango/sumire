package com.snowdango.sumire.repository.typeconverter

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class LocalDataTimeConverter {

    @OptIn(ExperimentalTime::class)
    @TypeConverter
    fun fromLocalDateTime(value: Long?): LocalDateTime? {
        return value?.let {
            val instant = Instant.fromEpochMilliseconds(it)
            instant.toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    @OptIn(ExperimentalTime::class)
    @TypeConverter
    fun toLocalDateTime(value: LocalDateTime?): Long? {
        return value?.toInstant(TimeZone.currentSystemDefault())?.toEpochMilliseconds()
    }
}
