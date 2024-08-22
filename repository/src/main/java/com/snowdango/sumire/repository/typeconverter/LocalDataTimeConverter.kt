package com.snowdango.sumire.repository.typeconverter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime


class LocalDataTimeConverter {

    @TypeConverter
    fun fromLocalDateTime(value: Long?): LocalDateTime? {
        return value?.let {
            val instant = Instant.fromEpochMilliseconds(it)
            instant.toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    @TypeConverter
    fun toLocalDateTime(value: LocalDateTime?): Long? {
        return value?.toInstant(TimeZone.currentSystemDefault())?.toEpochMilliseconds()
    }
}