package ru.netology.nework.api

import ru.netology.nework.dto.Job
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.User
import ru.netology.nework.dto.UserWithJobs
import retrofit2.Response
import retrofit2.http.*

interface UsersApi {
    @GET("api/users")
    suspend fun getAll(): Response<List<User>>

    @GET("api/users/{id}")
    suspend fun getById(@Path("id") id: Long): Response<UserWithJobs>

    @GET("api/users/{id}/wall")
    suspend fun getWall(@Path("id") id: Long): Response<List<Post>>

    @GET("api/users/{userId}/jobs")
    suspend fun getJobs(@Path("userId") userId: Long): Response<List<Job>>

    @POST("api/users/{userId}/jobs")
    suspend fun saveJob(@Path("userId") userId: Long, @Body job: Job): Response<Job>

    @DELETE("api/users/{userId}/jobs/{jobId}")
    suspend fun removeJob(@Path("userId") userId: Long, @Path("jobId") jobId: Long): Response<Unit>
}