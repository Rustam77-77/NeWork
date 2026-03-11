package ru.netology.nework.api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.dto.User

interface UsersApi {
    @GET("api/users")
    suspend fun getUsers(): Response<List<User>>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<User>

    @POST("api/users/registration")
    suspend fun registerUser(
        @Body body: RequestBody
    ): Response<User>
}