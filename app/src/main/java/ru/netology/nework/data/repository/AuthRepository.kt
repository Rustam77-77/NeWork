package ru.netology.nework.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nework.api.ApiService
import ru.netology.nework.dto.AuthResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        val token = tokenManager.getToken()
        val userId = tokenManager.getUserId()
        if (token != null && userId != null) {
            _authState.value = AuthState.Authenticated(token, userId)
        }
    }

    suspend fun login(login: String, password: String): Result<AuthResponse> {
        return try {
            // API_KEY теперь добавляется автоматически через ApiKeyInterceptor
            val response = apiService.login("", login, password)
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    tokenManager.saveToken(authResponse.token)
                    tokenManager.saveUserId(authResponse.userId)
                    _authState.value = AuthState.Authenticated(authResponse.token, authResponse.userId)
                    Result.success(authResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(login: String, name: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.register("", login, name, password)
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    tokenManager.saveToken(authResponse.token)
                    tokenManager.saveUserId(authResponse.userId)
                    _authState.value = AuthState.Authenticated(authResponse.token, authResponse.userId)
                    Result.success(authResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.clear()
        _authState.value = AuthState.Unauthenticated
    }

    fun isAuthenticated(): Boolean {
        return tokenManager.isAuthenticated()
    }

    val currentUserId: Long?
        get() = tokenManager.getUserId()

    fun getToken(): String? {
        return tokenManager.getToken()
    }
}

sealed class AuthState {
    data class Authenticated(val token: String, val userId: Long) : AuthState()
    object Unauthenticated : AuthState()
}