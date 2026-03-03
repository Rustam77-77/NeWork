package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.netology.nework.dto.Post
import ru.netology.nework.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

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

    fun likePost(post: Post) {
        viewModelScope.launch {
            postRepository.likePost(post)
        }
    }

    fun removePost(post: Post) {
        viewModelScope.launch {
            postRepository.removePost(post)
        }
    }

    fun clearError() {
        _error.value = null
    }
}