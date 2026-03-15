package ru.netology.nework.repository
import ru.netology.nework.api.ApiService
import ru.netology.nework.dto.Event
import javax.inject.Inject
class EventRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAll(): List<Event> =
        apiService.getAllEvents().body() ?: emptyList()
    suspend fun likeById(id: Long) = apiService.likeById(id)
    suspend fun dislikeById(id: Long) = apiService.dislikeById(id)
    suspend fun removeById(id: Long) = apiService.removeById(id)
}