package ru.netology.nework.repository

import android.util.Log
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

    companion object {
        private const val TAG = "EventRepository"
    }

    suspend fun loadEvents() {
        Log.d(TAG, "loadEvents: начата загрузка событий")
        _loading.value = true
        try {
            val response = eventsApi.getAll()
            Log.d(TAG, "loadEvents: код ответа = ${response.code()}")

            if (response.isSuccessful) {
                val events = response.body() ?: emptyList()
                Log.d(TAG, "loadEvents: успешно загружено ${events.size} событий")

                // Детальное логирование каждого события
                events.forEachIndexed { index, event ->
                    Log.d(TAG, "Событие #$index: id=${event.id}, type=${event.type}, " +
                            "datetime=${event.datetime}, author=${event.author}, " +
                            "content=${event.content.take(30)}...")

                    // Проверка на null поля type
                    if (event.type == null) {
                        Log.w(TAG, "ВНИМАНИЕ: У события id=${event.id} поле type = null!")
                    }

                    // Проверка на null других полей
                    if (event.authorJob == null) {
                        Log.d(TAG, "У события id=${event.id} нет информации о работе автора")
                    }

                    if (event.attachment != null) {
                        Log.d(TAG, "Событие id=${event.id} имеет вложение: ${event.attachment.type}")
                    }

                    if (!event.link.isNullOrBlank()) {
                        Log.d(TAG, "Событие id=${event.id} имеет ссылку: ${event.link}")
                    }

                    if (event.coords != null) {
                        Log.d(TAG, "Событие id=${event.id} имеет координаты: (${event.coords.lat}, ${event.coords.long})")
                    }

                    if (event.speakers.isNotEmpty()) {
                        Log.d(TAG, "Событие id=${event.id} имеет спикеров: ${event.speakers.size}")
                    }

                    if (event.participants.isNotEmpty()) {
                        Log.d(TAG, "Событие id=${event.id} имеет участников: ${event.participants.size}")
                    }
                }

                _events.value = events
                _error.value = null
                Log.i(TAG, "loadEvents: успешно завершена загрузка ${events.size} событий")
            } else {
                val errorMsg = "Ошибка загрузки: ${response.code()} - ${response.message()}"
                Log.e(TAG, "loadEvents: $errorMsg")
                _error.value = errorMsg

                // Логирование тела ошибки, если есть
                try {
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrBlank()) {
                        Log.e(TAG, "Тело ошибки: $errorBody")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Не удалось прочитать тело ошибки", e)
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "loadEvents: Ошибка сети", e)
            _error.value = "Ошибка сети. Проверьте подключение к интернету"
        } catch (e: Exception) {
            Log.e(TAG, "loadEvents: Неизвестная ошибка", e)
            _error.value = "Неизвестная ошибка: ${e.message}"
        } finally {
            _loading.value = false
            Log.d(TAG, "loadEvents: загрузка завершена, loading=false")
        }
    }

    suspend fun likeEvent(event: Event): Event? {
        Log.d(TAG, "likeEvent: начало, eventId=${event.id}, likedByMe=${event.likedByMe}")
        return try {
            val response = if (event.likedByMe) {
                Log.d(TAG, "likeEvent: снятие лайка с события ${event.id}")
                eventsApi.dislikeById(event.id)
            } else {
                Log.d(TAG, "likeEvent: постановка лайка на событие ${event.id}")
                eventsApi.likeById(event.id)
            }

            Log.d(TAG, "likeEvent: код ответа = ${response.code()}")

            if (response.isSuccessful) {
                val updatedEvent = response.body()
                if (updatedEvent != null) {
                    Log.d(TAG, "likeEvent: успешно, обновленное событие id=${updatedEvent.id}, likes=${updatedEvent.likes}")
                    updateEventInList(updatedEvent)
                } else {
                    Log.w(TAG, "likeEvent: ответ успешный, но тело пустое")
                }
                updatedEvent
            } else {
                val errorMsg = "Ошибка при ${if (event.likedByMe) "снятии" else "постановке"} лайка: ${response.code()}"
                Log.e(TAG, "likeEvent: $errorMsg")
                _error.value = errorMsg
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "likeEvent: Ошибка сети", e)
            _error.value = "Ошибка сети при выполнении операции"
            null
        } catch (e: Exception) {
            Log.e(TAG, "likeEvent: Неизвестная ошибка", e)
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    suspend fun removeEvent(event: Event): Boolean {
        Log.d(TAG, "removeEvent: начало, eventId=${event.id}")
        return try {
            val response = eventsApi.removeById(event.id)
            Log.d(TAG, "removeEvent: код ответа = ${response.code()}")

            if (response.isSuccessful) {
                Log.i(TAG, "removeEvent: событие ${event.id} успешно удалено")
                removeEventFromList(event.id)
                true
            } else {
                val errorMsg = "Ошибка при удалении: ${response.code()}"
                Log.e(TAG, "removeEvent: $errorMsg")
                _error.value = errorMsg
                false
            }
        } catch (e: IOException) {
            Log.e(TAG, "removeEvent: Ошибка сети", e)
            _error.value = "Ошибка сети при удалении"
            false
        } catch (e: Exception) {
            Log.e(TAG, "removeEvent: Неизвестная ошибка", e)
            _error.value = "Неизвестная ошибка: ${e.message}"
            false
        }
    }

    suspend fun saveEvent(event: Event): Event? {
        Log.d(TAG, "saveEvent: начало, eventId=${event.id}, content=${event.content.take(30)}...")
        return try {
            val response = eventsApi.save(event)
            Log.d(TAG, "saveEvent: код ответа = ${response.code()}")

            if (response.isSuccessful) {
                val savedEvent = response.body()
                if (savedEvent != null) {
                    Log.i(TAG, "saveEvent: событие успешно сохранено, id=${savedEvent.id}")
                    if (event.id == 0L) {
                        // Новое событие - добавляем в список
                        Log.d(TAG, "saveEvent: добавляем новое событие в список")
                        val currentList = _events.value.toMutableList()
                        currentList.add(0, savedEvent)
                        _events.value = currentList
                    } else {
                        // Обновление существующего события
                        Log.d(TAG, "saveEvent: обновляем существующее событие в списке")
                        updateEventInList(savedEvent)
                    }
                } else {
                    Log.w(TAG, "saveEvent: ответ успешный, но тело пустое")
                }
                savedEvent
            } else {
                val errorMsg = "Ошибка при сохранении: ${response.code()}"
                Log.e(TAG, "saveEvent: $errorMsg")
                _error.value = errorMsg
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "saveEvent: Ошибка сети", e)
            _error.value = "Ошибка сети при сохранении"
            null
        } catch (e: Exception) {
            Log.e(TAG, "saveEvent: Неизвестная ошибка", e)
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    suspend fun registerForEvent(event: Event): Event? {
        Log.d(TAG, "registerForEvent: начало, eventId=${event.id}")
        return try {
            val response = eventsApi.register(event.id)
            Log.d(TAG, "registerForEvent: код ответа = ${response.code()}")

            if (response.isSuccessful) {
                val updatedEvent = response.body()
                if (updatedEvent != null) {
                    Log.i(TAG, "registerForEvent: успешная регистрация на событие ${event.id}")
                    updateEventInList(updatedEvent)
                } else {
                    Log.w(TAG, "registerForEvent: ответ успешный, но тело пустое")
                }
                updatedEvent
            } else {
                val errorMsg = "Ошибка при регистрации: ${response.code()}"
                Log.e(TAG, "registerForEvent: $errorMsg")
                _error.value = errorMsg
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "registerForEvent: Ошибка сети", e)
            _error.value = "Ошибка сети при регистрации"
            null
        } catch (e: Exception) {
            Log.e(TAG, "registerForEvent: Неизвестная ошибка", e)
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    suspend fun unregisterFromEvent(event: Event): Event? {
        Log.d(TAG, "unregisterFromEvent: начало, eventId=${event.id}")
        return try {
            val response = eventsApi.unregister(event.id)
            Log.d(TAG, "unregisterFromEvent: код ответа = ${response.code()}")

            if (response.isSuccessful) {
                val updatedEvent = response.body()
                if (updatedEvent != null) {
                    Log.i(TAG, "unregisterFromEvent: успешная отмена регистрации на событие ${event.id}")
                    updateEventInList(updatedEvent)
                } else {
                    Log.w(TAG, "unregisterFromEvent: ответ успешный, но тело пустое")
                }
                updatedEvent
            } else {
                val errorMsg = "Ошибка при отмене регистрации: ${response.code()}"
                Log.e(TAG, "unregisterFromEvent: $errorMsg")
                _error.value = errorMsg
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "unregisterFromEvent: Ошибка сети", e)
            _error.value = "Ошибка сети при отмене регистрации"
            null
        } catch (e: Exception) {
            Log.e(TAG, "unregisterFromEvent: Неизвестная ошибка", e)
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    suspend fun getEventById(id: Long): Event? {
        Log.d(TAG, "getEventById: начало, id=$id")
        return try {
            val response = eventsApi.getById(id)
            Log.d(TAG, "getEventById: код ответа = ${response.code()}")

            if (response.isSuccessful) {
                val event = response.body()
                if (event != null) {
                    Log.d(TAG, "getEventById: событие найдено, type=${event.type}")
                } else {
                    Log.w(TAG, "getEventById: событие не найдено или тело пустое")
                }
                event
            } else {
                val errorMsg = "Ошибка при загрузке события: ${response.code()}"
                Log.e(TAG, "getEventById: $errorMsg")
                _error.value = errorMsg
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "getEventById: Ошибка сети", e)
            _error.value = "Ошибка сети при загрузке события"
            null
        } catch (e: Exception) {
            Log.e(TAG, "getEventById: Неизвестная ошибка", e)
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    private fun updateEventInList(updatedEvent: Event) {
        Log.d(TAG, "updateEventInList: обновление события ${updatedEvent.id} в списке")
        val currentList = _events.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedEvent.id }
        if (index != -1) {
            Log.d(TAG, "updateEventInList: событие найдено на позиции $index, обновляем")
            currentList[index] = updatedEvent
            _events.value = currentList
        } else {
            // Если события нет в списке (например, при обновлении с детального экрана)
            // Добавляем его в начало
            Log.d(TAG, "updateEventInList: событие не найдено в списке, добавляем в начало")
            currentList.add(0, updatedEvent)
            _events.value = currentList
        }
    }

    private fun removeEventFromList(eventId: Long) {
        Log.d(TAG, "removeEventFromList: удаление события $eventId из списка")
        val currentList = _events.value.toMutableList()
        val removed = currentList.removeAll { it.id == eventId }
        if (removed) {
            Log.d(TAG, "removeEventFromList: событие успешно удалено из списка")
            _events.value = currentList
        } else {
            Log.w(TAG, "removeEventFromList: событие $eventId не найдено в списке")
        }
    }

    fun clearError() {
        Log.d(TAG, "clearError: очистка ошибки")
        _error.value = null
    }
}