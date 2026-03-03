package ru.netology.nework.api

import ru.netology.nework.dto.Post
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PostsApi {
    @GET("api/posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("api/posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("api/posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("api/posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("api/posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("api/posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @Multipart
    @POST("api/media")
    suspend fun upload(@Part file: MultipartBody.Part): Response<MediaResponse>
}

data class MediaResponse(
    val id: String,
    val url: String
)