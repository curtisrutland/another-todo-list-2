package com.curtisrutland.atdl2.data.converter

import android.arch.persistence.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTimestamp(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }
}