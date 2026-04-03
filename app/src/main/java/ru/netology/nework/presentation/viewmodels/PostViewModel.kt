package ru.netology.nework.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.data.repository.PostRepository
import ru.netology.nework.dto.Post
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>(emptyList())
    val posts: LiveData<List<Post>> = _posts

    private val _post = MutableLiveData<Post?>()
    val post: LiveData<Post?> = _post

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isCreated = MutableLiveData(false)
    val isCreated: LiveData<Boolean> = _isCreated

    companion object {
        private const val TAG = "PostViewModel"
    }

    init {
        loadPosts()
        refreshPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val postsList = postRepository.getAllPosts()
                _posts.value = postsList
                Log.d(TAG, "Loaded ${postsList.size} posts")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading posts", e)
                _error.value = "Ошибка загрузки постов: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPostById(postId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val postItem = postRepository.getPostById(postId)
                _post.value = postItem
                Log.d(TAG, "Loaded post with id: $postId")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading post", e)
                _error.value = "Ошибка загрузки поста: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshPosts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                postRepository.refreshPosts()
                val postsList = postRepository.getAllPosts()
                _posts.value = postsList
                Log.d(TAG, "Refreshed ${postsList.size} posts")
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing posts", e)
                _error.value = "Ошибка обновления постов: ${e.message}"
            } finally {
                _isRefreshing.value = false
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
                    refreshPosts()
                    Log.d(TAG, "Post ${post.id} liked/unliked")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error liking post", e)
                _error.value = "Ошибка при лайке поста: ${e.message}"
            }
        }
    }

    fun createPost(content: String, mentionIds: List<Long>, authorId: Long, author: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _isCreated.value = false
            try {
                val post = Post(
                    id = 0,
                    authorId = authorId,
                    author = author,
                    content = content,
                    published = Instant.now(),
                    mentionIds = mentionIds,
                    likedByMe = false
                )
                val result = postRepository.savePost(post)
                if (result != null) {
                    _isCreated.value = true
                    refreshPosts()
                    Log.d(TAG, "Post created successfully")
                } else {
                    _error.value = "Ошибка создания поста"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating post", e)
                _error.value = "Ошибка создания поста: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePost(postId: Long, content: String, mentionIds: List<Long>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentPosts = _posts.value ?: emptyList()
                val existingPost = currentPosts.find { it.id == postId }
                if (existingPost != null) {
                    val updatedPost = existingPost.copy(
                        content = content,
                        mentionIds = mentionIds
                    )
                    val result = postRepository.savePost(updatedPost)
                    if (result != null) {
                        refreshPosts()
                        Log.d(TAG, "Post updated successfully")
                    } else {
                        _error.value = "Ошибка обновления поста"
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating post", e)
                _error.value = "Ошибка обновления поста: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            try {
                val result = postRepository.deletePost(postId)
                if (result) {
                    refreshPosts()
                    Log.d(TAG, "Post deleted successfully")
                } else {
                    _error.value = "Ошибка удаления поста"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting post", e)
                _error.value = "Ошибка удаления поста: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearCreated() {
        _isCreated.value = false
    }
}