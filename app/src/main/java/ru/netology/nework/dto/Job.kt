package ru.netology.nework.dto

import java.util.Date

data class Job(
    val id: Long,
    val userId: Long,
    val company: String,
    val position: String,
    val startDate: Date,
    val endDate: Date? = null
)