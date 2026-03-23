package ru.netology.nework.dto

data class LoginRequest(
    val login: String,
    val password: String
)

data class RegisterRequest(
    val login: String,
    val name: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val userId: Long
)