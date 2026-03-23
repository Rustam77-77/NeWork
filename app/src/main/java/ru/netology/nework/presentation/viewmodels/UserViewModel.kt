package ru.netology.nework.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.data.repository.UserRepository
import ru.netology.nework.dto.User
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>(emptyList())
    val users: LiveData<List<User>> = _users

    private val _selectedUser = MutableLiveData<User?>()
    val selectedUser: LiveData<User?> = _selectedUser

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.getAllUsers().collect { userList ->
                    _users.value = userList
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки пользователей: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserById(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.getUserById(userId).collect { user ->
                    _selectedUser.value = user
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки пользователя: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshUsers() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                userRepository.refreshUsers()
            } catch (e: Exception) {
                _error.value = "Ошибка обновления пользователей: ${e.message}"
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun refreshUserById(userId: Long) {
        viewModelScope.launch {
            try {
                userRepository.refreshUserById(userId)
            } catch (e: Exception) {
                _error.value = "Ошибка обновления пользователя: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}