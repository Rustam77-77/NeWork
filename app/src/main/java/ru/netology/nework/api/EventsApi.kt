package ru.netology.nework.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.dto.Event

interface EventsApi {
    @GET("api/events")
    suspend fun getEvents(): Response<List<Event>>

    @GET("api/events/{id}")
    suspend fun getEventById(@Path("id") id: Long): Response<Event>

    @POST("api/events")
    suspend fun saveEvent(@Body event: Event): Response<Event>

    @PUT("api/events/{id}")
    suspend fun updateEvent(@Path("id") id: Long, @Body event: Event): Response<Event>

    @DELETE("api/events/{id}")
    suspend fun removeEventById(@Path("id") id: Long): Response<Unit>

    @POST("api/events/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Event>

    @DELETE("api/events/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Event>

    @POST("api/events/{id}/participants")
    suspend fun participateInEvent(@Path("id") id: Long): Response<Event>

    @DELETE("api/events/{id}/participants")
    suspend fun leaveEvent(@Path("id") id: Long): Response<Event>
}