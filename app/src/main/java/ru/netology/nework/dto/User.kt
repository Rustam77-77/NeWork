package ru.netology.nework.dto

import java.io.Serializable

data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?
) : Serializable

<<<<<<< HEAD
=======
data class UserWithJobs(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?,
    val jobs: List<Job>
) : Serializable

>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
data class Job(
    val id: Long,
    val name: String,
    val position: String,
    val start: String,
    val finish: String?,
    val link: String?
<<<<<<< HEAD
) : Serializable

data class UserWithJobs(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?,
    val jobs: List<Job>
=======
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
) : Serializable