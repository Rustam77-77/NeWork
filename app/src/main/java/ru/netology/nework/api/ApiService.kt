package ru.netology.nework.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.dto.*

interface ApiService {

    // ==================== Auth ====================
    @FormUrlEncoded
    @POST("api/users/authentication")
    suspend fun login(
        @Header("API-KEY") apiKey: String,
        @Field("login") login: String,
        @Field("pass") password: String
    ): Response<AuthResponse>

    @FormUrlEncoded
    @POST("api/users/registration")
    suspend fun register(
        @Header("API-KEY") apiKey: String,
        @Field("login") login: String,
        @Field("name") name: String,
        @Field("pass") password: String
    ): Response<AuthResponse>

    // ==================== Posts ====================
    @GET("api/posts")
    suspend fun getAllPosts(
        @Header("API-KEY") apiKey: String
    ): Response<List<Post>>

    @GET("api/posts/{id}")
    suspend fun getPostById(
        @Header("API-KEY") apiKey: String,
        @Path("id") id: Long
    ): Response<Post>

    @POST("api/posts")
    suspend fun createPost(
        @Header("API-KEY") apiKey: String,
        @Body post: Post
    ): Response<Post>

    @PUT("api/posts/{id}")
    suspend fun updatePost(
        @Header("API-KEY") apiKey: String,
        @Path("id") id: Long,
        @Body post: Post
    ): Response<Post>

    @DELETE("api/posts/{id}")
    suspend fun deletePost(
        @Header("API-KEY") apiKey: String,
        @Path("id") id: Long
    ): Response<Unit>

    @POST("api/posts/{id}/likes")
    suspend fun likePost(
        @Header("API-KEY") apiKey: String,
        @Path("id") id: Long
    ): Response<Post>

    @DELETE("api/posts/{id}/likes")
    suspend fun unlikePost(
        @Header("API-KEY") apiKey: String,
        @Path("id") id: Long
    ): Response<Post>

    // ==================== Events ====================
    @GET("api/events")
    suspend fun getAllEvents(
        @Header("API-KEY") apiKey: String
    ): Response<List<Event>>

    @GET("api/events/{id}")
    suspend fun getEventById(
        @Header("API-KEY") apiKey: String,
        @Path("id") id: Long
    ): Response<Event>

    @POST("api/events")
    suspend fun createEvent(
        @Header("API-KEY") apiKey: String,
        @Body event: Event
    ): Response<Event>

    @PUT("api/events/{id}")
    suspend fun updateEvent(
        @Header("API-KEY") apiKey: String,
        @Path("id") id: Long,
        @Body event: Event
    ): Response<Event>

    @DELETE("api/events/{id}")
    suspend fun deleteEvent(
        @Header("API-KEY") apiKey: String,
        @Path("id") id: Long
    ): Response<Unit>

    @POST("api/events/{id}/likes")
    suspend fun likeEvent(
        @Header("API-KEY") apiKey: String,
        @Path("id") id: Long
    ): Response<Event>

    @DELETE("api/events/{id}/likes")
    suspend fun unlikeEvent(
        @Header("API-KEY") apiKey: String,
        @Path("id") id: Long
    ): Response<Event>

    // ==================== Users ====================
    @GET("api/users")
    suspend fun getAllUsers(
        @Header("API-KEY") apiKey: String
    ): Response<List<User>>

    @GET("api/users/{id}")
    suspend fun getUserById(
        @Header("API-KEY") apiKey: String,
        @Path("id") id: Long
    ): Response<User>

    // ==================== Jobs ====================
    @GET("api/users/{userId}/jobs")
    suspend fun getJobsForUser(
        @Header("API-KEY") apiKey: String,
        @Path("userId") userId: Long
    ): Response<List<Job>>

    @POST("api/jobs")
    suspend fun createJob(
        @Header("API-KEY") apiKey: String,
        @Body job: Job
    ): Response<Job>

    @DELETE("api/jobs/{id}")
    suspend fun deleteJob(
        @Header("API-KEY") apiKey: String,
        @Path("id") id: Long
    ): Response<Unit>
}