package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Token
import ru.netology.nework.dto.User
import ru.netology.nework.model.AuthState
import ru.netology.nework.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authorized: LiveData<Boolean> = authRepository.authorized
    val user: LiveData<User?> = authRepository.user
    val token: LiveData<Token?> = authRepository.token
    val authError: LiveData<String?> = authRepository.authError
    val regError: LiveData<String?> = authRepository.regError
    val currentUserId: LiveData<Long?> = authRepository.currentUserId
    val isAuthenticated: LiveData<Boolean> = authRepository.isAuthenticated

    private val _authState = MutableLiveData<AuthState>(AuthState.IDLE)
    val authState: LiveData<AuthState> = _authState

    fun authenticate(login: String, pass: String) {
        _authState.value = AuthState.LOADING
        viewModelScope.launch {
            val result = authRepository.authenticate(login, pass)
            if (result.isSuccess) {
                _authState.value = AuthState.SUCCESS
            } else {
                _authState.value = AuthState.ERROR
            }
        }
    }

    fun register(login: String, pass: String, name: String) {
        _authState.value = AuthState.LOADING
        viewModelScope.launch {
            val result = authRepository.register(login, pass, name)
            if (result.isSuccess) {
                _authState.value = AuthState.SUCCESS
            } else {
                _authState.value = AuthState.ERROR
            }
        }
    }

    fun getUserById(id: String) {
        viewModelScope.launch {
            authRepository.getUserById(id)
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.IDLE
    }

    fun clearAuthError() {
        authRepository.clearAuthError()
    }

    fun clearRegError() {
        authRepository.clearRegError()
    }

    fun clearError() {
        authRepository.clearError()
    }
}