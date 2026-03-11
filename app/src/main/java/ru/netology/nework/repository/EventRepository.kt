package ru.netology.nework.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import ru.netology.nework.api.EventsApi
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.NetworkError
import ru.netology.nework.error.UnknownError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventsApi: EventsApi
) {
    private val _events = MutableLiveData<List<Event>>(emptyList())
    val events: LiveData<List<Event>> = _events

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadEvents()
    }

    fun loadEvents() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _loading.postValue(true)
                Log.d("EventRepository", "Loading events...")

                val response = eventsApi.getEvents()

                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EventRepository", "Error: ${response.code()} - $errorBody")
                    _error.postValue("Ошибка загрузки: ${response.code()}")
                    _loading.postValue(false)
                    return@launch
                }

                val events = response.body()
                if (events != null) {
                    Log.d("EventRepository", "Events loaded: ${events.size}")

                    val processedEvents = events.map { event ->
                        event.copy(
                            type = event.type ?: EventType.OFFLINE,
                            likedByMe = event.likedByMe ?: false,
                            participatedByMe = event.participatedByMe ?: false
                        )
                    }

                    _events.postValue(processedEvents)
                    _error.postValue(null)
                } else {
                    Log.e("EventRepository", "Events list is null")
                    _error.postValue("Получен пустой список событий")
                }
            } catch (e: Exception) {
                Log.e("EventRepository", "Exception loading events: ${e.message}", e)
                _error.postValue("Неизвестная ошибка: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }

    suspend fun likeById(id: Long): Result<Event> = withContext(Dispatchers.IO) {
        try {
            Log.d("EventRepository", "Liking event: $id")
            val response = eventsApi.likeById(id)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("EventRepository", "Error liking: ${response.code()} - $errorBody")
                return@withContext Result.failure(ApiError(response.code(), errorBody ?: "Unknown error"))
            }

            val event = response.body()
            if (event != null) {
                Log.d("EventRepository", "Event liked successfully")
                updateEventInList(event)
                Result.success(event)
            } else {
                Log.e("EventRepository", "Event is null after liking")
                Result.failure(UnknownError)
            }
        } catch (e: Exception) {
            Log.e("EventRepository", "Exception liking: ${e.message}", e)
            Result.failure(NetworkError)
        }
    }

    suspend fun dislikeById(id: Long): Result<Event> = withContext(Dispatchers.IO) {
        try {
            Log.d("EventRepository", "Disliking event: $id")
            val response = eventsApi.dislikeById(id)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("EventRepository", "Error disliking: ${response.code()} - $errorBody")
                return@withContext Result.failure(ApiError(response.code(), errorBody ?: "Unknown error"))
            }

            val event = response.body()
            if (event != null) {
                Log.d("EventRepository", "Event disliked successfully")
                updateEventInList(event)
                Result.success(event)
            } else {
                Log.e("EventRepository", "Event is null after disliking")
                Result.failure(UnknownError)
            }
        } catch (e: Exception) {
            Log.e("EventRepository", "Exception disliking: ${e.message}", e)
            Result.failure(NetworkError)
        }
    }

    suspend fun participateInEvent(id: Long): Result<Event> = withContext(Dispatchers.IO) {
        try {
            Log.d("EventRepository", "Participating in event: $id")
            val response = eventsApi.participateInEvent(id)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("EventRepository", "Error participating: ${response.code()} - $errorBody")
                return@withContext Result.failure(ApiError(response.code(), errorBody ?: "Unknown error"))
            }

            val event = response.body()
            if (event != null) {
                Log.d("EventRepository", "Participated successfully")
                updateEventInList(event)
                Result.success(event)
            } else {
                Log.e("EventRepository", "Event is null after participating")
                Result.failure(UnknownError)
            }
        } catch (e: Exception) {
            Log.e("EventRepository", "Exception participating: ${e.message}", e)
            Result.failure(NetworkError)
        }
    }

    suspend fun leaveEvent(id: Long): Result<Event> = withContext(Dispatchers.IO) {
        try {
            Log.d("EventRepository", "Leaving event: $id")
            val response = eventsApi.leaveEvent(id)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("EventRepository", "Error leaving: ${response.code()} - $errorBody")
                return@withContext Result.failure(ApiError(response.code(), errorBody ?: "Unknown error"))
            }

            val event = response.body()
            if (event != null) {
                Log.d("EventRepository", "Left successfully")
                updateEventInList(event)
                Result.success(event)
            } else {
                Log.e("EventRepository", "Event is null after leaving")
                Result.failure(UnknownError)
            }
        } catch (e: Exception) {
            Log.e("EventRepository", "Exception leaving: ${e.message}", e)
            Result.failure(NetworkError)
        }
    }

    suspend fun removeEventById(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("EventRepository", "Removing event: $id")
            val response = eventsApi.removeEventById(id)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("EventRepository", "Error removing: ${response.code()} - $errorBody")
                return@withContext Result.failure(ApiError(response.code(), errorBody ?: "Unknown error"))
            }

            Log.d("EventRepository", "Event removed successfully")
            removeEventFromList(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("EventRepository", "Exception removing: ${e.message}", e)
            Result.failure(NetworkError)
        }
    }

    private fun updateEventInList(updatedEvent: Event) {
        val currentList = _events.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.id == updatedEvent.id }
        if (index != -1) {
            currentList[index] = updatedEvent
            _events.postValue(currentList)
        }
    }

    private fun removeEventFromList(id: Long) {
        val currentList = _events.value?.toMutableList() ?: return
        currentList.removeAll { it.id == id }
        _events.postValue(currentList)
    }

    fun clearError() {
        _error.postValue(null)
    }
}