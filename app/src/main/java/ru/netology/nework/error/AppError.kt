package ru.netology.nework.error

import java.io.IOException

class ApiError(val code: Int, message: String) : IOException(message)
object NetworkError : IOException("Network error")
object UnknownError : IOException("Unknown error")