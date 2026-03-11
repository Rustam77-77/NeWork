package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Event
import ru.netology.nework.repository.EventRepository
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    val events: LiveData<List<Event>> = eventRepository.events
    val loading: LiveData<Boolean> = eventRepository.loading
    val error: LiveData<String?> = eventRepository.error

    fun loadEvents() {
        eventRepository.loadEvents()
    }

    fun clearError() {
        eventRepository.clearError()
    }

    fun likeEvent(event: Event) {
        viewModelScope.launch {
            if (event.likedByMe == true) {
                eventRepository.dislikeById(event.id)
            } else {
                eventRepository.likeById(event.id)
            }
        }
    }

    fun participateEvent(event: Event) {
        viewModelScope.launch {
            if (event.participatedByMe == true) {
                eventRepository.leaveEvent(event.id)
            } else {
                eventRepository.participateInEvent(event.id)
            }
        }
    }

    fun removeEvent(event: Event) {
        viewModelScope.launch {
            eventRepository.removeEventById(event.id)
        }
    }
}