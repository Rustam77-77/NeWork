package ru.netology.nework.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.data.repository.EventRepository
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import java.time.Instant
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

    companion object {
        private const val TAG = "EventViewModel"
    }

    init {
        loadEvents()
        refreshEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                eventRepository.getAllEvents().collect { eventList ->
                    _events.value = eventList
                    Log.d(TAG, "Loaded ${eventList.size} events")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading events", e)
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
                    Log.d(TAG, "Loaded event with id: $eventId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading event", e)
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
                Log.d(TAG, "Events refreshed")
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing events", e)
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
                    Log.d(TAG, "Event ${event.id} liked/unliked")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error liking event", e)
                _error.value = "Ошибка при лайке события: ${e.message}"
            }
        }
    }

    fun createEvent(
        content: String,
        datetime: Instant,
        type: EventType,
        speakerIds: List<Long>,
        participantsIds: List<Long>,
        authorId: Long,
        author: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _isCreated.value = false
            try {
                val result = eventRepository.createEvent(
                    content = content,
                    datetime = datetime,
                    type = type,
                    speakerIds = speakerIds,
                    participantsIds = participantsIds,
                    authorId = authorId,
                    author = author
                )
                if (result != null) {
                    _isCreated.value = true
                    refreshEvents()
                    Log.d(TAG, "Event created successfully")
                } else {
                    _error.value = "Ошибка создания события"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating event", e)
                _error.value = "Ошибка создания события: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateEvent(
        eventId: Long,
        content: String,
        datetime: Instant,
        type: EventType,
        speakerIds: List<Long>,
        participantsIds: List<Long>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = eventRepository.updateEvent(
                    id = eventId,
                    content = content,
                    datetime = datetime,
                    type = type,
                    speakerIds = speakerIds,
                    participantsIds = participantsIds
                )
                if (result != null) {
                    refreshEvents()
                    Log.d(TAG, "Event updated successfully")
                } else {
                    _error.value = "Ошибка обновления события"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating event", e)
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
                    Log.d(TAG, "Event deleted successfully")
                } else {
                    _error.value = "Ошибка удаления события"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting event", e)
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