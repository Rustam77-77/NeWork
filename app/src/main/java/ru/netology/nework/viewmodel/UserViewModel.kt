package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.User
import ru.netology.nework.dto.UserWithJobs
import ru.netology.nework.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _selectedUser = MutableLiveData<UserWithJobs?>()
    val selectedUser: LiveData<UserWithJobs?> = _selectedUser

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        viewModelScope.launch {
            userRepository.users.collect { users ->
                _users.postValue(users)
            }
        }

        viewModelScope.launch {
            userRepository.loading.collect { isLoading ->
                _loading.postValue(isLoading)
            }
        }

        viewModelScope.launch {
            userRepository.error.collect { errorMsg ->
                _error.postValue(errorMsg)
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            userRepository.loadUsers()
        }
    }

    fun getUserById(userId: Long) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val testUser = UserWithJobs(
                    id = userId,
                    login = "user$userId",
                    name = "User $userId",
                    avatar = null,
                    jobs = emptyList()
                )
                _selectedUser.postValue(testUser)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки пользователя: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}