package ru.netology.nework.dto

import java.io.Serializable

data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?
) : Serializable

data class Job(
    val id: Long,
    val name: String,
    val position: String,
    val start: String,
    val finish: String?,
    val link: String?
) : Serializable

data class UserWithJobs(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?,
    val jobs: List<Job>
) : Serializable