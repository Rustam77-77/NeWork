package ru.netology.nework.repository

import ru.netology.nework.api.PostsApi
import ru.netology.nework.dto.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val postsApi: PostsApi
) {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    suspend fun loadPosts() {
        _loading.value = true
        try {
            val response = postsApi.getAll()
            if (response.isSuccessful) {
                _posts.value = response.body() ?: emptyList()
                _error.value = null
            } else {
                _error.value = "Ошибка загрузки: ${response.code()}"
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети. Проверьте подключение к интернету"
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
        } finally {
            _loading.value = false
        }
    }

    suspend fun likePost(post: Post): Post? {
        return try {
            val response = if (post.likedByMe == true) {
                postsApi.dislikeById(post.id)
            } else {
                postsApi.likeById(post.id)
            }

            if (response.isSuccessful) {
                val updatedPost = response.body()
                if (updatedPost != null) {
                    updatePostInList(updatedPost)
                }
                updatedPost
            } else {
                _error.value = "Ошибка при ${if (post.likedByMe == true) "снятии" else "постановке"} лайка: ${response.code()}"
                null
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при выполнении операции"
            null
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    suspend fun removePost(post: Post): Boolean {
        return try {
            val response = postsApi.removeById(post.id)
            if (response.isSuccessful) {
                removePostFromList(post.id)
                true
            } else {
                _error.value = "Ошибка при удалении: ${response.code()}"
                false
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при удалении"
            false
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            false
        }
    }

    private fun updatePostInList(updatedPost: Post) {
        val currentList = _posts.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedPost.id }
        if (index != -1) {
            currentList[index] = updatedPost
            _posts.value = currentList
        }
    }

    private fun removePostFromList(postId: Long) {
        val currentList = _posts.value.toMutableList()
        currentList.removeAll { it.id == postId }
        _posts.value = currentList
    }

    fun clearError() {
        _error.value = null
    }
}