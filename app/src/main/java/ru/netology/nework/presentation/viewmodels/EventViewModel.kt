package ru.netology.nework.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.data.repository.EventRepository
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _events = MutableLiveData<List<Event>>(emptyList())
    val events: LiveData<List<Event>> = _events

    private val _event = MutableLiveData<Event?>()
    val event: LiveData<Event?> = _event

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isCreated = MutableLiveData(false)
    val isCreated: LiveData<Boolean> = _isCreated

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                eventRepository.getAllEvents().collect { eventList ->
                    _events.value = eventList
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки событий: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadEventById(eventId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                eventRepository.getEventById(eventId).collect { event ->
                    _event.value = event
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки события: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshEvents() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                eventRepository.refreshEvents()
            } catch (e: Exception) {
                _error.value = "Ошибка обновления событий: ${e.message}"
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun likeEvent(event: Event) {
        viewModelScope.launch {
            try {
                val updatedEvent = if (event.likedByMe) {
                    eventRepository.unlikeEvent(event.id)
                } else {
                    eventRepository.likeEvent(event.id)
                }
                if (updatedEvent != null) {
                    refreshEvents()
                }
            } catch (e: Exception) {
                _error.value = "Ошибка при лайке события"
            }
        }
    }

    fun createEvent(
        content: String,
        datetime: Date,
        type: EventType,
        speakerIds: List<Long>,
        participantIds: List<Long>,
        authorId: Long,
        author: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _isCreated.value = false
            try {
                val event = Event(
                    id = 0,
                    authorId = authorId,
                    author = author,
                    content = content,
                    published = Date(),
                    datetime = datetime,
                    type = type,
                    speakerIds = speakerIds,
                    participantIds = participantIds
                )
                val result = eventRepository.saveEvent(event)
                if (result != null) {
                    _isCreated.value = true
                    refreshEvents()
                } else {
                    _error.value = "Ошибка создания события"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка создания события: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateEvent(
        eventId: Long,
        content: String,
        datetime: Date,
        type: EventType,
        speakerIds: List<Long>,
        participantIds: List<Long>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentEvents = _events.value ?: emptyList()
                val existingEvent = currentEvents.find { it.id == eventId }
                if (existingEvent != null) {
                    val updatedEvent = existingEvent.copy(
                        content = content,
                        datetime = datetime,
                        type = type,
                        speakerIds = speakerIds,
                        participantIds = participantIds
                    )
                    val result = eventRepository.saveEvent(updatedEvent)
                    if (result != null) {
                        refreshEvents()
                    } else {
                        _error.value = "Ошибка обновления события"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Ошибка обновления события: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteEvent(eventId: Long) {
        viewModelScope.launch {
            try {
                val result = eventRepository.deleteEvent(eventId)
                if (result) {
                    refreshEvents()
                } else {
                    _error.value = "Ошибка удаления события"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка удаления события: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearCreated() {
        _isCreated.value = false
    }
}