package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.netology.nework.dto.Post
import ru.netology.nework.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserWallViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadUserWall(userId: Long) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = userRepository.getUserWall(userId)
                _posts.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки стены: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}