package ru.netology.nework.api

import ru.netology.nework.dto.Event
import retrofit2.Response
import retrofit2.http.*

interface EventsApi {
    @GET("api/events")
    suspend fun getAll(): Response<List<Event>>

    @GET("api/events/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Event>

    @POST("api/events")
    suspend fun save(@Body event: Event): Response<Event>

    @DELETE("api/events/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("api/events/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Event>

    @DELETE("api/events/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Event>

    @POST("api/events/{id}/participants")
    suspend fun register(@Path("id") id: Long): Response<Event>

    @DELETE("api/events/{id}/participants")
    suspend fun unregister(@Path("id") id: Long): Response<Event>
}