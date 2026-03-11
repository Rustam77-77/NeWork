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
<<<<<<< HEAD
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
=======
            println("Loading events from API...")
            val response = eventsApi.getAll()
            println("Response code: ${response.code()}")

            if (response.isSuccessful) {
                val eventsList = response.body()
                println("Events loaded: ${eventsList?.size ?: 0}")

                // Безопасная обработка - всегда сохраняем не-null список
                _events.value = eventsList ?: emptyList()
                _error.value = null
            } else {
                val errorBody = response.errorBody()?.string()
                println("Error response: $errorBody")
                _error.value = "Ошибка загрузки событий: ${response.code()}"
            }
        } catch (e: IOException) {
            println("Network error: ${e.message}")
            _error.value = "Ошибка сети. Проверьте подключение к интернету"
        } catch (e: Exception) {
            println("Unexpected error: ${e.message}")
            e.printStackTrace()
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            _error.value = "Неизвестная ошибка: ${e.message}"
        } finally {
            _loading.value = false
        }
    }

<<<<<<< HEAD
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
=======
    suspend fun likeEvent(event: Event): Event? {
        return try {
            val response = if (event.likedByMe) {
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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
<<<<<<< HEAD
                _error.value = "Ошибка при ${if (event.likedByMe == true) "снятии" else "постановке"} лайка: ${response.code()}"
=======
                _error.value = "Ошибка при ${if (event.likedByMe) "снятии" else "постановке"} лайка: ${response.code()}"
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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
<<<<<<< HEAD
=======
                        // Новое событие - добавляем в список
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                        val currentList = _events.value.toMutableList()
                        currentList.add(0, savedEvent)
                        _events.value = currentList
                    } else {
<<<<<<< HEAD
=======
                        // Обновление существующего события
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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

<<<<<<< HEAD
=======
    suspend fun getEventById(id: Long): Event? {
        return try {
            val response = eventsApi.getById(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                _error.value = "Ошибка при загрузке события: ${response.code()}"
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

>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
    private fun updateEventInList(updatedEvent: Event) {
        val currentList = _events.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedEvent.id }
        if (index != -1) {
            currentList[index] = updatedEvent
            _events.value = currentList
        } else {
<<<<<<< HEAD
            val newList = mutableListOf(updatedEvent) + currentList
=======
            // Если события нет в списке, добавляем его в начало
            val newList = mutableListOf(updatedEvent)
            newList.addAll(currentList)
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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