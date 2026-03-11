package ru.netology.nework.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?,
    val token: String? = null
) : Parcelable {
    companion object {
        fun getStoredUser(): User? {
            return null
        }

        fun clearUser() {
            // Временно ничего не делаем
        }
    }

    fun saveUser() {
        // Временно ничего не делаем
    }
}