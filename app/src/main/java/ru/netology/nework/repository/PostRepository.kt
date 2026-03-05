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

    suspend fun likeById(postId: Long): Post? {
        return try {
            val response = postsApi.likeById(postId)
            if (response.isSuccessful) {
                val post = response.body()
                if (post != null) {
                    updatePostInList(post)
                }
                post
            } else {
                _error.value = "Ошибка при лайке: ${response.code()}"
                null
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при лайке"
            null
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    suspend fun dislikeById(postId: Long): Post? {
        return try {
            val response = postsApi.dislikeById(postId)
            if (response.isSuccessful) {
                val post = response.body()
                if (post != null) {
                    updatePostInList(post)
                }
                post
            } else {
                _error.value = "Ошибка при снятии лайка: ${response.code()}"
                null
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при снятии лайка"
            null
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    suspend fun removeById(postId: Long): Boolean {
        return try {
            val response = postsApi.removeById(postId)
            if (response.isSuccessful) {
                removePostFromList(postId)
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

    suspend fun savePost(post: Post): Post? {
        return try {
            val response = postsApi.save(post)
            if (response.isSuccessful) {
                val savedPost = response.body()
                if (savedPost != null) {
                    if (post.id == 0L) {
                        // Новый пост - добавляем в начало списка
                        val currentList = _posts.value.toMutableList()
                        currentList.add(0, savedPost)
                        _posts.value = currentList
                    } else {
                        // Обновление существующего поста
                        updatePostInList(savedPost)
                    }
                }
                savedPost
            } else {
                _error.value = "Ошибка при сохранении: ${response.code()}"
                null
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при сохранении"
            null
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    suspend fun getPostById(postId: Long): Post? {
        return try {
            val response = postsApi.getById(postId)
            if (response.isSuccessful) {
                response.body()
            } else {
                _error.value = "Ошибка при загрузке поста: ${response.code()}"
                null
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при загрузке поста"
            null
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
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