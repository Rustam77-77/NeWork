package ru.netology.nework.presentation.viewmodels

import android.util.Log
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

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    companion object {
        private const val TAG = "UserViewModel"
    }

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val usersList = userRepository.getAllUsers()
                _users.value = usersList
                Log.d(TAG, "Loaded ${usersList.size} users")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading users", e)
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
                val userItem = userRepository.getUserById(userId)
                _user.value = userItem
                Log.d(TAG, "Loaded user with id: $userId")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user", e)
                _error.value = "Ошибка загрузки пользователя: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshUsers() {
        viewModelScope.launch {
            try {
                userRepository.refreshUsers()
                val usersList = userRepository.getAllUsers()
                _users.value = usersList
                Log.d(TAG, "Users refreshed")
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing users", e)
                _error.value = "Ошибка обновления пользователей: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}