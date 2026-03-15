package ru.netology.nework.api
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.dto.*
interface ApiService {
    @GET("events")
    suspend fun getAllEvents(): Response<List<Event>>
    @POST("events/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Event>
    @DELETE("events/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Event>
    @DELETE("events/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>
    @POST("users/authentication")
    @FormUrlEncoded
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<AuthState>
    @POST("users/registration")
    @FormUrlEncoded
    suspend fun registerUser(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<AuthState>
}