package ru.netology.nework.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.dto.*

interface ApiService {

    // ==================== Auth ====================
    @FormUrlEncoded
    @POST("api/users/authentication")
    suspend fun login(
        @Field("login") login: String,
        @Field("pass") password: String  // Изменено с password на pass
    ): Response<AuthResponse>

    @FormUrlEncoded
    @POST("api/users/registration")  // Изменен эндпоинт
    suspend fun register(
        @Field("login") login: String,
        @Field("name") name: String,
        @Field("pass") password: String  // Изменено с password на pass
    ): Response<AuthResponse>

    // ==================== Posts ====================
    @GET("api/posts")
    suspend fun getAllPosts(): Response<List<Post>>

    @GET("api/posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Response<Post>

    @POST("api/posts")
    suspend fun createPost(@Body post: Post): Response<Post>

    @PUT("api/posts/{id}")
    suspend fun updatePost(@Path("id") id: Long, @Body post: Post): Response<Post>

    @DELETE("api/posts/{id}")
    suspend fun deletePost(@Path("id") id: Long): Response<Unit>

    @POST("api/posts/{id}/likes")
    suspend fun likePost(@Path("id") id: Long): Response<Post>

    @DELETE("api/posts/{id}/likes")
    suspend fun unlikePost(@Path("id") id: Long): Response<Post>

    // ==================== Events ====================
    @GET("api/events")
    suspend fun getAllEvents(): Response<List<Event>>

    @GET("api/events/{id}")
    suspend fun getEventById(@Path("id") id: Long): Response<Event>

    @POST("api/events")
    suspend fun createEvent(@Body event: Event): Response<Event>

    @PUT("api/events/{id}")
    suspend fun updateEvent(@Path("id") id: Long, @Body event: Event): Response<Event>

    @DELETE("api/events/{id}")
    suspend fun deleteEvent(@Path("id") id: Long): Response<Unit>

    @POST("api/events/{id}/likes")
    suspend fun likeEvent(@Path("id") id: Long): Response<Event>

    @DELETE("api/events/{id}/likes")
    suspend fun unlikeEvent(@Path("id") id: Long): Response<Event>

    // ==================== Users ====================
    @GET("api/users")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>

    // ==================== Jobs ====================
    @GET("api/users/{userId}/jobs")
    suspend fun getJobsForUser(@Path("userId") userId: Long): Response<List<Job>>

    @POST("api/jobs")
    suspend fun createJob(@Body job: Job): Response<Job>

    @DELETE("api/jobs/{id}")
    suspend fun deleteJob(@Path("id") id: Long): Response<Unit>
}