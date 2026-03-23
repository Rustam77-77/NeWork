package ru.netology.nework.presentation.viewmodels

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

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        _isAuthenticated.value = authRepository.isAuthenticated()
        _currentUserId.value = authRepository.currentUserId
    }

    fun login(login: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = authRepository.login(login, password)
                if (result.isSuccess) {
                    _isAuthenticated.value = true
                    _currentUserId.value = result.getOrNull()?.userId
                } else {
                    _error.value = "Неправильный логин или пароль"
                }
            } catch (e: Exception) {
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
            try {
                val result = authRepository.register(login, name, password)
                if (result.isSuccess) {
                    _isAuthenticated.value = true
                    _currentUserId.value = result.getOrNull()?.userId
                } else {
                    _error.value = "Пользователь с таким логином уже зарегистрирован"
                }
            } catch (e: Exception) {
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
    }

    fun clearError() {
        _error.value = null
    }
}