package ru.netology.nework.viewmodel
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Job
import ru.netology.nework.repository.UserRepository
import javax.inject.Inject
@HiltViewModel
class UserJobsViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val _jobs = MutableLiveData<List<Job>>()
    val jobs: LiveData<List<Job>> = _jobs
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error
    val jobCreated = MutableLiveData<Unit>()
    fun loadUserJobs(userId: Long) = viewModelScope.launch {
        try {
            _jobs.value = repository.getUserJobs(userId)
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
    fun saveJob(job: Job) = viewModelScope.launch {
        try {
            repository.saveJob(job)
            jobCreated.value = Unit
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
    fun deleteJob(id: Long) = viewModelScope.launch {
        try {
            repository.deleteJob(id)
            // Список обновится сам при следующем входе или можно вызвать loadUserJobs вручную
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
}