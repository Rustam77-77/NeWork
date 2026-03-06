package ru.netology.nework.repository

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import ru.netology.nework.api.AuthApi
import ru.netology.nework.dto.Credentials
import ru.netology.nework.dto.RegisterCredentials
import ru.netology.nework.dto.Token
import ru.netology.nework.error.AppError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val prefs: SharedPreferences,
    private val gson: Gson
) {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    private val _currentUserLogin = MutableStateFlow<String?>(null)
    val currentUserLogin: StateFlow<String?> = _currentUserLogin.asStateFlow()

    private val _currentUserName = MutableStateFlow<String?>(null)
    val currentUserName: StateFlow<String?> = _currentUserName.asStateFlow()

    init {
        val token = prefs.getString(KEY_TOKEN, null)
        val userId = if (prefs.contains(KEY_USER_ID)) prefs.getLong(KEY_USER_ID, -1) else -1
        val userLogin = prefs.getString(KEY_USER_LOGIN, null)
        val userName = prefs.getString(KEY_USER_NAME, null)

        if (token != null && userId != -1L) {
            _authToken.value = token
            _currentUserId.value = userId
            _currentUserLogin.value = userLogin
            _currentUserName.value = userName
            _isAuthenticated.value = true
        }
    }

    suspend fun authenticate(login: String, password: String): Token {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("AuthRepository", "Attempting authentication for login: $login")

                val credentials = Credentials(login, password)
                Log.d("AuthRepository", "Sending credentials: $credentials")

                val response = authApi.authentication(credentials)
                Log.d("AuthRepository", "Response code: ${response.code()}")

                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AuthRepository", "Error: ${response.code()} - $errorBody")
                    throw AppError.ApiError(response.code(), errorBody ?: response.message())
                }

                val token = response.body()
                Log.d("AuthRepository", "Token received: $token")

                if (token == null) {
                    throw AppError.UnknownError
                }

                saveAuthData(token, login, "")
                token
            } catch (e: IOException) {
                Log.e("AuthRepository", "Network error", e)
                throw AppError.NetworkError
            } catch (e: Exception) {
                Log.e("AuthRepository", "Unexpected error", e)
                throw e
            }
        }
    }

    suspend fun register(login: String, password: String, name: String): Token {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("AuthRepository", "Attempting registration for login: $login, name: $name")

                val credentials = RegisterCredentials(login, password, name)
                val response = authApi.registration(credentials)

                Log.d("AuthRepository", "Response code: ${response.code()}")

                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AuthRepository", "Error: ${response.code()} - $errorBody")
                    throw AppError.ApiError(response.code(), errorBody ?: response.message())
                }

                val token = response.body()
                Log.d("AuthRepository", "Token received: $token")

                if (token == null) {
                    throw AppError.UnknownError
                }

                saveAuthData(token, login, name)
                token
            } catch (e: IOException) {
                Log.e("AuthRepository", "Network error", e)
                throw AppError.NetworkError
            } catch (e: Exception) {
                Log.e("AuthRepository", "Unexpected error", e)
                throw e
            }
        }
    }

    suspend fun registerWithAvatar(
        login: String,
        password: String,
        name: String,
        avatarPart: MultipartBody.Part
    ): Token {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("AuthRepository", "Attempting registration with avatar for login: $login")

                val response = authApi.registerWithAvatar(login, password, name, avatarPart)

                Log.d("AuthRepository", "Response code: ${response.code()}")

                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AuthRepository", "Error: ${response.code()} - $errorBody")
                    throw AppError.ApiError(response.code(), errorBody ?: response.message())
                }

                val token = response.body()
                Log.d("AuthRepository", "Token received: $token")

                if (token == null) {
                    throw AppError.UnknownError
                }

                saveAuthData(token, login, name)
                token
            } catch (e: IOException) {
                Log.e("AuthRepository", "Network error", e)
                throw AppError.NetworkError
            } catch (e: Exception) {
                Log.e("AuthRepository", "Unexpected error", e)
                throw e
            }
        }
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            prefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_USER_ID)
                .remove(KEY_USER_LOGIN)
                .remove(KEY_USER_NAME)
                .apply()

            _authToken.value = null
            _currentUserId.value = null
            _currentUserLogin.value = null
            _currentUserName.value = null
            _isAuthenticated.value = false

            Log.d("AuthRepository", "User logged out")
        }
    }

    private fun saveAuthData(token: Token, login: String, name: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token.token)
            .putLong(KEY_USER_ID, token.id)
            .putString(KEY_USER_LOGIN, login)
            .putString(KEY_USER_NAME, name)
            .apply()

        _authToken.value = token.token
        _currentUserId.value = token.id
        _currentUserLogin.value = login
        _currentUserName.value = name
        _isAuthenticated.value = true

        Log.d("AuthRepository", "Auth data saved for user: $login (ID: ${token.id})")
    }

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_LOGIN = "user_login"
        private const val KEY_USER_NAME = "user_name"
    }
}