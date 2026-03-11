package ru.netology.nework.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import ru.netology.nework.api.PostsApi
import ru.netology.nework.dto.Post
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.NetworkError
import ru.netology.nework.error.UnknownError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val postsApi: PostsApi
) {
    private val _posts = MutableLiveData<List<Post>>(emptyList())
    val posts: LiveData<List<Post>> = _posts

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadPosts()
    }

    fun loadPosts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _loading.postValue(true)
                Log.d("PostRepository", "Loading posts...")

                val response = postsApi.getPosts()

                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PostRepository", "Error: ${response.code()} - $errorBody")
                    _error.postValue("Ошибка загрузки: ${response.code()}")
                    _loading.postValue(false)
                    return@launch
                }

                val posts = response.body()
                if (posts != null) {
                    Log.d("PostRepository", "Posts loaded: ${posts.size}")

                    val processedPosts = posts.map { post ->
                        post.copy(
                            likedByMe = post.likedByMe ?: false
                        )
                    }

                    _posts.postValue(processedPosts)
                    _error.postValue(null)
                } else {
                    Log.e("PostRepository", "Posts list is null")
                    _error.postValue("Получен пустой список постов")
                }
            } catch (e: Exception) {
                Log.e("PostRepository", "Exception loading posts: ${e.message}", e)
                _error.postValue("Неизвестная ошибка: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }
}