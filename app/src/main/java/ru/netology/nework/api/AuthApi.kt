package ru.netology.nework.api

import ru.netology.nework.dto.Credentials
import ru.netology.nework.dto.RegisterCredentials
import ru.netology.nework.dto.Token
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("api/users/authentication")
    suspend fun authentication(@Body credentials: Credentials): Response<Token>

    @POST("api/users/registration")
    suspend fun registration(@Body credentials: RegisterCredentials): Response<Token>

    @Multipart
    @POST("api/users/registration")
    suspend fun registerWithAvatar(
        @Part("login") login: String,
        @Part("pass") pass: String,
        @Part("name") name: String,
        @Part file: MultipartBody.Part
    ): Response<Token>
}