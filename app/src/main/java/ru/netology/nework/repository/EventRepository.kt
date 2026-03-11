package ru.netology.nework.repository

import ru.netology.nework.api.EventsApi
import ru.netology.nework.dto.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventsApi: EventsApi
) {
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    suspend fun loadEvents() {
        _loading.value = true
        try {
            val response = eventsApi.getAll()
            if (response.isSuccessful) {
                val body = response.body()
                _events.value = body ?: emptyList()
                _error.value = null
            } else {
                _error.value = "Ошибка загрузки: ${response.code()}"
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети. Проверьте подключение к интернету"
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
        } finally {
            _loading.value = false
        }
    }

    suspend fun getEventById(id: Long): Event? {
        return try {
            val response = eventsApi.getById(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                _error.value = "Ошибка загрузки события: ${response.code()}"
                null
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при загрузке события"
            null
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    suspend fun likeEvent(event: Event): Event? {
        return try {
            val response = if (event.likedByMe == true) {
                eventsApi.dislikeById(event.id)
            } else {
                eventsApi.likeById(event.id)
            }

            if (response.isSuccessful) {
                val updatedEvent = response.body()
                if (updatedEvent != null) {
                    updateEventInList(updatedEvent)
                }
                updatedEvent
            } else {
                _error.value = "Ошибка при ${if (event.likedByMe == true) "снятии" else "постановке"} лайка: ${response.code()}"
                null
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при выполнении операции"
            null
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    suspend fun removeEvent(event: Event): Boolean {
        return try {
            val response = eventsApi.removeById(event.id)
            if (response.isSuccessful) {
                removeEventFromList(event.id)
                true
            } else {
                _error.value = "Ошибка при удалении: ${response.code()}"
                false
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при удалении"
            false
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            false
        }
    }

    suspend fun saveEvent(event: Event): Event? {
        return try {
            val response = eventsApi.save(event)
            if (response.isSuccessful) {
                val savedEvent = response.body()
                if (savedEvent != null) {
                    if (event.id == 0L) {
                        val currentList = _events.value.toMutableList()
                        currentList.add(0, savedEvent)
                        _events.value = currentList
                    } else {
                        updateEventInList(savedEvent)
                    }
                }
                savedEvent
            } else {
                _error.value = "Ошибка при сохранении: ${response.code()}"
                null
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при сохранении"
            null
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    suspend fun registerForEvent(event: Event): Event? {
        return try {
            val response = eventsApi.register(event.id)
            if (response.isSuccessful) {
                val updatedEvent = response.body()
                if (updatedEvent != null) {
                    updateEventInList(updatedEvent)
                }
                updatedEvent
            } else {
                _error.value = "Ошибка при регистрации: ${response.code()}"
                null
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при регистрации"
            null
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    suspend fun unregisterFromEvent(event: Event): Event? {
        return try {
            val response = eventsApi.unregister(event.id)
            if (response.isSuccessful) {
                val updatedEvent = response.body()
                if (updatedEvent != null) {
                    updateEventInList(updatedEvent)
                }
                updatedEvent
            } else {
                _error.value = "Ошибка при отмене регистрации: ${response.code()}"
                null
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при отмене регистрации"
            null
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    private fun updateEventInList(updatedEvent: Event) {
        val currentList = _events.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedEvent.id }
        if (index != -1) {
            currentList[index] = updatedEvent
            _events.value = currentList
        } else {
            val newList = mutableListOf(updatedEvent) + currentList
            _events.value = newList
        }
    }

    private fun removeEventFromList(eventId: Long) {
        val currentList = _events.value.toMutableList()
        currentList.removeAll { it.id == eventId }
        _events.value = currentList
    }

    fun clearError() {
        _error.value = null
    }
}