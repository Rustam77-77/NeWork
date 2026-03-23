package ru.netology.nework.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.data.repository.PostRepository
import ru.netology.nework.dto.Post
import javax.inject.Inject

@HiltViewModel
class UserWallViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>(emptyList())
    val posts: LiveData<List<Post>> = _posts

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentUserId: Long = 0

    fun loadUserPosts(userId: Long) {
        currentUserId = userId
        viewModelScope.launch {
            _isLoading.value = true
            try {
                postRepository.getAllPosts().collect { allPosts ->
                    val userPosts = allPosts.filter { it.authorId == userId }
                    _posts.value = userPosts
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки постов пользователя: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshUserPosts() {
        if (currentUserId != 0L) {
            viewModelScope.launch {
                try {
                    postRepository.refreshPosts()
                } catch (e: Exception) {
                    _error.value = "Ошибка обновления постов: ${e.message}"
                }
            }
        }
    }

    fun likePost(post: Post) {
        viewModelScope.launch {
            try {
                val updatedPost = if (post.likedByMe) {
                    postRepository.unlikePost(post.id)
                } else {
                    postRepository.likePost(post.id)
                }
                if (updatedPost != null) {
                    refreshUserPosts()
                }
            } catch (e: Exception) {
                _error.value = "Ошибка при лайке поста"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}