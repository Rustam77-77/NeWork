package ru.netology.nework.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Token(
    val id: Long,
    val token: String
) : Parcelable {
    companion object {
        fun getStoredToken(): Token? {
            return null
        }

        fun clearToken() {
            // Временно ничего не делаем
        }
    }

    fun saveToken() {
        // Временно ничего не делаем
    }
}