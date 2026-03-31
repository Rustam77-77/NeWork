package ru.netology.nework.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginRequest(
    val login: String,
    val password: String
) : Parcelable

@Parcelize
data class RegisterRequest(
    val login: String,
    val name: String,
    val password: String
) : Parcelable

@Parcelize
data class AuthResponse(
    val token: String,
    val userId: Long
) : Parcelable