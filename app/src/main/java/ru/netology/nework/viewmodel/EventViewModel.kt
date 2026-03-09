package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.netology.nework.dto.Event
import ru.netology.nework.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _selectedEvent = MutableLiveData<Event?>()
    val selectedEvent: LiveData<Event?> = _selectedEvent

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        viewModelScope.launch {
            eventRepository.events.collect { events ->
                _events.postValue(events)
            }
        }

        viewModelScope.launch {
            eventRepository.loading.collect { isLoading ->
                _loading.postValue(isLoading)
            }
        }

        viewModelScope.launch {
            eventRepository.error.collect { errorMsg ->
                _error.postValue(errorMsg)
            }
        }
    }

    fun loadEvents() {
        viewModelScope.launch {
            eventRepository.loadEvents()
        }
    }

    fun getEventById(id: Long) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val event = eventRepository.getEventById(id)
                _selectedEvent.postValue(event)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки события: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun likeEvent(event: Event) {
        viewModelScope.launch {
            val updatedEvent = eventRepository.likeEvent(event)
            if (updatedEvent != null) {
                // Обновляем выбранное событие, если это оно
                if (_selectedEvent.value?.id == event.id) {
                    _selectedEvent.postValue(updatedEvent)
                }
            }
        }
    }

    fun removeEvent(event: Event) {
        viewModelScope.launch {
            val success = eventRepository.removeEvent(event)
            if (success) {
                loadEvents() // Перезагружаем список после удаления
            }
        }
    }

    fun saveEvent(event: Event) {
        viewModelScope.launch {
            val savedEvent = eventRepository.saveEvent(event)
            if (savedEvent != null) {
                loadEvents() // Перезагружаем список после сохранения
            }
        }
    }

    fun registerForEvent(event: Event) {
        viewModelScope.launch {
            val updatedEvent = eventRepository.registerForEvent(event)
            if (updatedEvent != null) {
                if (_selectedEvent.value?.id == event.id) {
                    _selectedEvent.postValue(updatedEvent)
                }
                loadEvents()
            }
        }
    }

    fun unregisterFromEvent(event: Event) {
        viewModelScope.launch {
            val updatedEvent = eventRepository.unregisterFromEvent(event)
            if (updatedEvent != null) {
                if (_selectedEvent.value?.id == event.id) {
                    _selectedEvent.postValue(updatedEvent)
                }
                loadEvents()
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}