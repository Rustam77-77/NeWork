package ru.netology.nework.utils

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    fun formatDate(dateString: String): String {
        return try {
            // Парсим дату из формата сервера
            val inputFormat = when {
                dateString.contains(".") -> {
                    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                }
                dateString.contains("T") -> {
                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    format.timeZone = TimeZone.getTimeZone("UTC")
                    format
                }
                else -> {
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                }
            }

            val date = if (dateString.contains("T")) {
                inputFormat.parse(dateString)
            } else {
                inputFormat.parse(dateString)
            }

            // Форматируем в нужный вид
            val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getDefault()
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatEventDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)

            val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getDefault()
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatJobPeriod(start: String, finish: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDate = inputFormat.parse(start)

            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("ru"))
            val startStr = outputFormat.format(startDate ?: Date())

            if (finish != null) {
                val finishDate = inputFormat.parse(finish)
                val finishStr = outputFormat.format(finishDate ?: Date())
                "$startStr - $finishStr"
            } else {
                "$startStr - настоящее время"
            }
        } catch (e: Exception) {
            "$start - ${finish ?: "настоящее время"}"
        }
    }
}