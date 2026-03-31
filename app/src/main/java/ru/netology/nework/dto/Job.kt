package ru.netology.nework.dto

import android.os.Parcelable
import com.google.gson.annotations.JsonAdapter
import kotlinx.parcelize.Parcelize
import ru.netology.nework.utils.InstantAdapter
import java.time.Instant

@Parcelize
data class Job(
    val id: Long,
    val userId: Long,
    val company: String,
    val position: String,
    @JsonAdapter(InstantAdapter::class)
    val startDate: Instant,
    @JsonAdapter(InstantAdapter::class)
    val endDate: Instant? = null
) : Parcelable