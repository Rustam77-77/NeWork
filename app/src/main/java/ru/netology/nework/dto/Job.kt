package ru.netology.nework.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Job(
    val id: Long,
    val userId: Long,
    val company: String,
    val position: String,
    val startDate: Date,
    val endDate: Date? = null
) : Parcelable