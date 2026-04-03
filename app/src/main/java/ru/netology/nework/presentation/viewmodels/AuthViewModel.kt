package ru.netology.nework.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isAuthenticated = MutableLiveData(false)
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _currentUserId = MutableLiveData<Long?>()
    val currentUserId: LiveData<Long?> = _currentUserId

    companion object {
        private const val TAG = "AuthViewModel"
    }

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        _isAuthenticated.value = authRepository.isAuthenticated()
        _currentUserId.value = authRepository.currentUserId
        Log.d(TAG, "Auth status: ${_isAuthenticated.value}")
    }

    fun login(login: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d(TAG, "Login attempt for: $login")
            try {
                val result = authRepository.login(login, password)
                if (result.isSuccess) {
                    _isAuthenticated.value = true
                    _currentUserId.value = result.getOrNull()?.userId
                    Log.d(TAG, "Login successful for: $login")
                } else {
                    val error = result.exceptionOrNull()
                    _error.value = error?.message ?: "Неправильный логин или пароль"
                    Log.e(TAG, "Login failed: ${error?.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login error", e)
                _error.value = "Ошибка входа: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(login: String, name: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            _error.value = "Пароли не совпадают"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            Log.d(TAG, "Register attempt for: $login")
            try {
                val result = authRepository.register(login, name, password)
                if (result.isSuccess) {
                    _isAuthenticated.value = true
                    _currentUserId.value = result.getOrNull()?.userId
                    Log.d(TAG, "Register successful for: $login")
                } else {
                    val error = result.exceptionOrNull()
                    _error.value = error?.message ?: "Пользователь с таким логином уже зарегистрирован"
                    Log.e(TAG, "Register failed: ${error?.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Register error", e)
                _error.value = "Ошибка регистрации: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _isAuthenticated.value = false
        _currentUserId.value = null
        Log.d(TAG, "Logged out")
    }

    fun clearError() {
        _error.value = null
    }
}