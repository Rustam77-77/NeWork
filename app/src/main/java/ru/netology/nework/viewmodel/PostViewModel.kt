package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Post
import ru.netology.nework.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _selectedPost = MutableLiveData<Post?>()
    val selectedPost: LiveData<Post?> = _selectedPost

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        viewModelScope.launch {
            postRepository.posts.collect { posts ->
                _posts.postValue(posts)
            }
        }

        viewModelScope.launch {
            postRepository.loading.collect { isLoading ->
                _loading.postValue(isLoading)
            }
        }

        viewModelScope.launch {
            postRepository.error.collect { errorMsg ->
                _error.postValue(errorMsg)
            }
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            postRepository.loadPosts()
        }
    }

    fun getPostById(postId: Long) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val post = postRepository.getPostById(postId)
                _selectedPost.postValue(post)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки поста: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun likePost(postId: Long, likedByMe: Boolean) {
        viewModelScope.launch {
            val updatedPost = if (likedByMe) {
                postRepository.likeById(postId)
            } else {
                postRepository.dislikeById(postId)
            }

            if (updatedPost != null) {
                // Обновляем выбранный пост, если это он
                if (_selectedPost.value?.id == postId) {
                    _selectedPost.postValue(updatedPost)
                }
            }
        }
    }

    fun removePost(postId: Long) {
        viewModelScope.launch {
            val success = postRepository.removeById(postId)
            if (success) {
                loadPosts() // Перезагружаем список после удаления
            }
        }
    }

    fun savePost(post: Post) {
        viewModelScope.launch {
            val savedPost = postRepository.savePost(post)
            if (savedPost != null) {
                loadPosts() // Перезагружаем список после сохранения
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}