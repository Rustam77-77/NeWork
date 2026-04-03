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
    val name: String,
    val position: String,
    @JsonAdapter(InstantAdapter::class)
    val start: Instant,
    @JsonAdapter(InstantAdapter::class)
    val finish: Instant? = null,
    val link: String? = null
) : Parcelable