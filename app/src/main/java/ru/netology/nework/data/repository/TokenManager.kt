package ru.netology.nework.data.repository

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val TOKEN_KEY = "auth_token"
        private const val USER_ID_KEY = "user_id"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getUserId(): Long? {
        val id = prefs.getLong(USER_ID_KEY, -1L)
        return if (id != -1L) id else null
    }

    fun saveUserId(userId: Long) {
        prefs.edit().putLong(USER_ID_KEY, userId).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun isAuthenticated(): Boolean {
        return getToken() != null
    }
}