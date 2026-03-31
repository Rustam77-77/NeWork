package ru.netology.nework.utils

import com.google.gson.*
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter : JsonDeserializer<Date>, JsonSerializer<Date> {

    private val dateFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
        SimpleDateFormat("yyyy-MM-dd", Locale.US)
    ).apply {
        forEach { it.timeZone = TimeZone.getTimeZone("UTC") }
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Date {
        val dateString = json.asJsonPrimitive.asString

        // Обработка некорректной даты +58191-05-18T14:27:41Z
        val cleanedString = if (dateString.startsWith("+")) {
            dateString.substring(1)
        } else {
            dateString
        }

        return try {
            dateFormats.forEach { format ->
                try {
                    return format.parse(cleanedString) ?: Date()
                } catch (e: ParseException) {
                    // Продолжаем
                }
            }
            Date()
        } catch (e: Exception) {
            Date()
        }
    }

    override fun serialize(
        src: Date,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        return JsonPrimitive(format.format(src))
    }
}