package ru.netology.nework.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    fun getAllPosts(): Flow<List<Post>> =
        postDao.getAllPosts().map { entities -> entities.map { it.toModel() } }

    fun getPostById(postId: Long): Flow<Post?> =
        postDao.getPostById(postId).map { entity -> entity?.toModel() }

    suspend fun refreshPosts() {
        try {
            val response = apiService.getAllPosts()
            if (response.isSuccessful) {
                response.body()?.let { posts ->
                    postDao.insertAll(posts.map { it.toEntity() })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun likePost(postId: Long): Post? {
        return try {
            val response = apiService.likePost(postId)
            if (response.isSuccessful) {
                response.body()?.also { updatedPost ->
                    postDao.insert(updatedPost.toEntity())
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun unlikePost(postId: Long): Post? {
        return try {
            val response = apiService.unlikePost(postId)
            if (response.isSuccessful) {
                response.body()?.also { updatedPost ->
                    postDao.insert(updatedPost.toEntity())
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun savePost(post: Post): Post? {
        return try {
            val response = if (post.id == 0L) {
                apiService.createPost(post)
            } else {
                apiService.updatePost(post.id, post)
            }

            if (response.isSuccessful) {
                response.body()?.also { updatedPost ->
                    postDao.insert(updatedPost.toEntity())
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deletePost(postId: Long): Boolean {
        return try {
            val response = apiService.deletePost(postId)
            if (response.isSuccessful) {
                postDao.deleteById(postId)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}