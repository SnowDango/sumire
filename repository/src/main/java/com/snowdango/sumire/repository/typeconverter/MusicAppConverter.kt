package com.snowdango.sumire.repository.typeconverter

import androidx.room.TypeConverter
import com.snowdango.sumire.data.entity.MusicApp

class MusicAppConverter {

    @TypeConverter
    fun fromMusicApp(value: String?): MusicApp? {
        return value?.let {
            MusicApp.entries.first { app -> app.platform == it }
        }
    }

    @TypeConverter
    fun toMusicApp(app: MusicApp?): String? {
        return app?.platform
    }
}