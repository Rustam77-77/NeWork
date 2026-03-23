package ru.netology.nework.data.repository

import ru.netology.nework.api.ApiService
import ru.netology.nework.dto.AuthResponse
import ru.netology.nework.dto.LoginRequest
import ru.netology.nework.dto.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    private var _authToken: String? = null
    private var _currentUserId: Long? = null

    val authToken: String? get() = _authToken
    val currentUserId: Long? get() = _currentUserId

    suspend fun login(login: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.login(LoginRequest(login, password))
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    _authToken = authResponse.token
                    _currentUserId = authResponse.userId
                    Result.success(authResponse)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(login: String, name: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.register(RegisterRequest(login, name, password))
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    _authToken = authResponse.token
                    _currentUserId = authResponse.userId
                    Result.success(authResponse)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        _authToken = null
        _currentUserId = null
    }

    fun isAuthenticated(): Boolean = _authToken != null
}