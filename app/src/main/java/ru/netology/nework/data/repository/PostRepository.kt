package ru.netology.nework.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.data.database.dao.PostDao
import ru.netology.nework.data.database.entities.PostEntity
import ru.netology.nework.data.database.entities.toEntity
import ru.netology.nework.data.database.entities.toModel
import ru.netology.nework.dto.Post
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PostRepository"

@Singleton
class PostRepository @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService
) {
    fun getAllPosts(): Flow<List<Post>> =
        postDao.getAllPosts().map { entities ->
            entities.map { it.toModel() }
        }

    fun getPostById(postId: Long): Flow<Post?> =
        postDao.getPostById(postId).map { entity ->
            entity?.toModel()
        }

    suspend fun refreshPosts() {
        try {
            Log.d(TAG, "refreshPosts: starting network request")
            val response = apiService.getAllPosts()
            Log.d(TAG, "refreshPosts: response code = ${response.code()}")

            if (response.isSuccessful) {
                response.body()?.let { posts ->
                    Log.d(TAG, "refreshPosts: received ${posts.size} posts from server")

                    posts.forEach { post ->
                        Log.d(TAG, "Post: id=${post.id}, author=${post.author}")
                    }

                    val entities = posts.map { it.toEntity() }
                    postDao.insertAll(entities)
                    Log.d(TAG, "refreshPosts: saved ${entities.size} posts to DB")
                } ?: run {
                    Log.e(TAG, "refreshPosts: response body is null")
                }
            } else {
                Log.e(TAG, "refreshPosts: error response ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "refreshPosts: exception", e)
        }
    }

    suspend fun likePost(postId: Long): Post? {
        return try {
            Log.d(TAG, "likePost: postId=$postId")
            val response = apiService.likePost(postId)
            if (response.isSuccessful) {
                response.body()?.also { post ->
                    postDao.insert(post.toEntity())
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "likePost: exception", e)
            null
        }
    }

    suspend fun unlikePost(postId: Long): Post? {
        return try {
            Log.d(TAG, "unlikePost: postId=$postId")
            val response = apiService.unlikePost(postId)
            if (response.isSuccessful) {
                response.body()?.also { post ->
                    postDao.insert(post.toEntity())
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "unlikePost: exception", e)
            null
        }
    }

    suspend fun savePost(post: Post): Post? {
        return try {
            Log.d(TAG, "savePost: postId=${post.id}")
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
            Log.e(TAG, "savePost: exception", e)
            null
        }
    }

    suspend fun deletePost(postId: Long): Boolean {
        return try {
            Log.d(TAG, "deletePost: postId=$postId")
            val response = apiService.deletePost(postId)
            if (response.isSuccessful) {
                postDao.deleteById(postId)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "deletePost: exception", e)
            false
        }
    }
}