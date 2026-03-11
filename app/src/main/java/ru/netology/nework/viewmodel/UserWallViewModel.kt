package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Post
import ru.netology.nework.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserWallViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>(emptyList())
    val posts: LiveData<List<Post>> = _posts

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadWall(userId: Long) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val result = userRepository.getUserWall(userId)
                result.onSuccess { posts ->
                    _posts.value = posts
                    _error.value = null
                }.onFailure { exception ->
                    _error.value = exception.message
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}