package ru.netology.nework.viewmodel
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Event
import ru.netology.nework.repository.EventRepository
import javax.inject.Inject
@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error
    fun loadEvents() = viewModelScope.launch {
        try {
            _events.value = repository.getAll()
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id) // Раньше id игнорировался
            loadEvents() // Обновляем список после лайка
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id) // Раньше id игнорировался
            loadEvents()
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
}