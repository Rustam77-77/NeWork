package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Post
import ru.netology.nework.repository.PostRepository
import ru.netology.nework.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserWallViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
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

    fun likePost(postId: Long, likedByMe: Boolean) {
        viewModelScope.launch {
            try {
                val updatedPost = if (likedByMe) {
                    postRepository.likeById(postId)
                } else {
                    postRepository.dislikeById(postId)
                }

                if (updatedPost != null) {
                    updatePostInList(updatedPost)
                } else {
                    _error.value = "Не удалось выполнить операцию"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка при выполнении операции: ${e.message}"
            }
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            try {
                val success = postRepository.removeById(postId)
                if (success) {
                    removePostFromList(postId)
                } else {
                    _error.value = "Ошибка при удалении поста"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка при удалении: ${e.message}"
            }
        }
    }

    private fun updatePostInList(updatedPost: Post) {
        val currentList = _posts.value?.toMutableList() ?: mutableListOf()
        val index = currentList.indexOfFirst { it.id == updatedPost.id }
        if (index != -1) {
            currentList[index] = updatedPost
            _posts.value = currentList
        }
    }

    private fun removePostFromList(postId: Long) {
        val currentList = _posts.value?.toMutableList() ?: mutableListOf()
        currentList.removeAll { it.id == postId }
        _posts.value = currentList
    }

    fun clearError() {
        _error.value = null
    }
}