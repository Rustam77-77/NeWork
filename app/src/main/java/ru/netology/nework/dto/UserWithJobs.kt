package ru.netology.nework.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserWithJobs(
    val user: User,
    val jobs: List<Job>
) : Parcelable