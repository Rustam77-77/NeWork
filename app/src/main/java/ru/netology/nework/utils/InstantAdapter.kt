package ru.netology.nework.utils

import com.google.gson.*
import java.lang.reflect.Type
import java.time.Instant
import java.time.format.DateTimeFormatter

class InstantAdapter : JsonDeserializer<Instant>, JsonSerializer<Instant> {

    private val formatter = DateTimeFormatter.ISO_INSTANT

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Instant {
        val dateString = json.asJsonPrimitive.asString
        // Обработка даты с префиксом "+"
        val cleanedString = if (dateString.startsWith("+")) {
            dateString.substring(1)
        } else {
            dateString
        }
        return try {
            Instant.parse(cleanedString)
        } catch (e: Exception) {
            Instant.now()
        }
    }

    override fun serialize(
        src: Instant,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(formatter.format(src))
    }
}