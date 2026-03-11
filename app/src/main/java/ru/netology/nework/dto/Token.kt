package ru.netology.nework.dto

data class Token(
    val id: Long,
    val token: String
)

data class Credentials(
    val login: String,
<<<<<<< HEAD
    val password: String
=======
    val pass: String
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
)

data class RegisterCredentials(
    val login: String,
<<<<<<< HEAD
    val password: String,
=======
    val pass: String,
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
    val name: String
)