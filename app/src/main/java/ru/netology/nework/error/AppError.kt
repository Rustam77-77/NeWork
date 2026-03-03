package ru.netology.nework.error

sealed class AppError : RuntimeException() {
    object NetworkError : AppError()
    object UnknownError : AppError()
    data class ApiError(val code: Int, override val message: String) : AppError()
}