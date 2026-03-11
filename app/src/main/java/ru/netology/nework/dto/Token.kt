package ru.netology.nework.dto

data class Token(
    val id: Long,
    val token: String
)

data class Credentials(
    val login: String,
    val password: String
)

data class RegisterCredentials(
    val login: String,
    val password: String,
    val name: String
)