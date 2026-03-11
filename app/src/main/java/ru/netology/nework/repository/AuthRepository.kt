package ru.netology.nework.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nework.api.AuthApi
import ru.netology.nework.api.UsersApi
import ru.netology.nework.dto.Token
import ru.netology.nework.dto.User
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.NetworkError
import ru.netology.nework.error.UnknownError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val usersApi: UsersApi,
) {
    private val _authorized = MutableLiveData(false)
    val authorized: LiveData<Boolean> = _authorized

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _token = MutableLiveData<Token?>()
    val token: LiveData<Token?> = _token

    private val _authError = MutableLiveData<String?>()
    val authError: LiveData<String?> = _authError

    private val _regError = MutableLiveData<String?>()
    val regError: LiveData<String?> = _regError

    private val _currentUserId = MutableLiveData<Long?>()
    val currentUserId: LiveData<Long?> = _currentUserId

    private val _isAuthenticated = MutableLiveData(false)
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    init {
        loadStoredUserData()
    }

    private fun loadStoredUserData() {
        _authorized.postValue(false)
        _isAuthenticated.postValue(false)
        _currentUserId.postValue(null)
    }

    suspend fun authenticate(login: String, pass: String): Result<Token> = withContext(Dispatchers.IO) {
        try {
            Log.d("AuthRepository", "Authenticating user: $login")

            val json = """
                {
                    "login": "$login",
                    "password": "$pass"
                }
            """.trimIndent()

            val requestBody = json.toRequestBody("application/json".toMediaType())

            val response = authApi.authentication(requestBody)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepository", "Error: ${response.code()} - $errorBody")
                return@withContext Result.failure(ApiError(response.code(), errorBody ?: "Unknown error"))
            }

            val token = response.body()
            if (token != null) {
                _token.postValue(token)
                _authorized.postValue(true)
                _isAuthenticated.postValue(true)
                _authError.postValue(null)
                Result.success(token)
            } else {
                Result.failure(UnknownError)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Exception: ${e.message}", e)
            Result.failure(NetworkError)
        }
    }

    suspend fun register(login: String, pass: String, name: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val json = """
                {
                    "login": "$login",
                    "password": "$pass",
                    "name": "$name"
                }
            """.trimIndent()

            val requestBody = json.toRequestBody("application/json".toMediaType())

            val response = usersApi.registerUser(requestBody)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                return@withContext Result.failure(ApiError(response.code(), errorBody ?: "Unknown error"))
            }

            val user = response.body()
            if (user != null) {
                _regError.postValue(null)
                Result.success(user)
            } else {
                Result.failure(UnknownError)
            }
        } catch (e: Exception) {
            Result.failure(NetworkError)
        }
    }

    suspend fun getUserById(id: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = usersApi.getUserById(id)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                return@withContext Result.failure(ApiError(response.code(), errorBody ?: "Unknown error"))
            }

            val user = response.body()
            if (user != null) {
                _user.postValue(user)
                _currentUserId.postValue(user.id)
                Result.success(user)
            } else {
                Result.failure(UnknownError)
            }
        } catch (e: Exception) {
            Result.failure(NetworkError)
        }
    }

    fun logout() {
        _token.postValue(null)
        _user.postValue(null)
        _authorized.postValue(false)
        _isAuthenticated.postValue(false)
        _currentUserId.postValue(null)
    }

    fun clearAuthError() {
        _authError.postValue(null)
    }

    fun clearRegError() {
        _regError.postValue(null)
    }

    fun clearError() {
        _authError.postValue(null)
        _regError.postValue(null)
    }
}