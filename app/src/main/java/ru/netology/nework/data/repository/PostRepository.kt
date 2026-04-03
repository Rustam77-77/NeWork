package ru.netology.nework.data.repository

import kotlinx.coroutines.flow.firstOrNull
import ru.netology.nework.api.ApiService
import ru.netology.nework.data.database.dao.PostDao
import ru.netology.nework.data.database.entities.toEntity
import ru.netology.nework.data.database.entities.toModel
import ru.netology.nework.dto.Post
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService
) {

    suspend fun getAllPosts(): List<Post> {
        return try {
            val response = apiService.getAllPosts("")
            if (response.isSuccessful) {
                response.body()?.let { posts ->
                    postDao.insertAll(posts.map { it.toEntity() })
                    posts
                } ?: emptyList()
            } else {
                val postsFlow = postDao.getAll()
                postsFlow.firstOrNull()?.map { it.toModel() } ?: emptyList()
            }
        } catch (e: Exception) {
            val postsFlow = postDao.getAll()
            postsFlow.firstOrNull()?.map { it.toModel() } ?: emptyList()
        }
    }

    suspend fun getPostById(id: Long): Post? {
        return try {
            val response = apiService.getPostById("", id)
            if (response.isSuccessful) {
                response.body()?.also { post ->
                    postDao.insert(post.toEntity())
                }
            } else {
                postDao.getById(id)?.toModel()
            }
        } catch (e: Exception) {
            postDao.getById(id)?.toModel()
        }
    }

    suspend fun refreshPosts() {
        try {
            val response = apiService.getAllPosts("")
            if (response.isSuccessful) {
                response.body()?.let { posts ->
                    postDao.insertAll(posts.map { it.toEntity() })
                }
            }
        } catch (e: Exception) {
            // Обработка ошибки
        }
    }

    suspend fun likePost(id: Long): Post? {
        return try {
            val response = apiService.likePost("", id)
            if (response.isSuccessful) {
                response.body()?.also { post ->
                    postDao.insert(post.toEntity())
                }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun unlikePost(id: Long): Post? {
        return try {
            val response = apiService.unlikePost("", id)
            if (response.isSuccessful) {
                response.body()?.also { post ->
                    postDao.insert(post.toEntity())
                }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun savePost(post: Post): Post? {
        return try {
            val response = if (post.id == 0L) {
                apiService.createPost("", post)
            } else {
                apiService.updatePost("", post.id, post)
            }
            if (response.isSuccessful) {
                response.body()?.also { newPost ->
                    postDao.insert(newPost.toEntity())
                }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deletePost(id: Long): Boolean {
        return try {
            val response = apiService.deletePost("", id)
            if (response.isSuccessful) {
                postDao.deleteById(id)
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }
}