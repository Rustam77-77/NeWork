package ru.netology.nework.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.dto.Post

interface PostsApi {
    @GET("api/posts")
    suspend fun getPosts(): Response<List<Post>>

    @GET("api/posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Response<Post>

    @POST("api/posts")
    suspend fun savePost(@Body post: Post): Response<Post>

    @PUT("api/posts/{id}")
    suspend fun updatePost(@Path("id") id: Long, @Body post: Post): Response<Post>

    @DELETE("api/posts/{id}")
    suspend fun removePostById(@Path("id") id: Long): Response<Unit>

    @POST("api/posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("api/posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>
}