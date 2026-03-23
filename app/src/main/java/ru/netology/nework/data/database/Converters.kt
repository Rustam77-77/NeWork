package ru.netology.nework.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nework.dto.EventType
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromStringList(value: String): List<Long> {
        if (value.isEmpty()) return emptyList()
        val listType = object : TypeToken<List<Long>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListLong(list: List<Long>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromEventType(type: EventType): String {
        return type.name
    }

    @TypeConverter
    fun toEventType(type: String): EventType {
        return EventType.valueOf(type)
    }
}