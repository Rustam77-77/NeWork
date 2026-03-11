package ru.netology.nework.api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.dto.Token

interface AuthApi {
    @POST("api/users/authentication")
    suspend fun authentication(
        @Body body: RequestBody
    ): Response<Token>
}