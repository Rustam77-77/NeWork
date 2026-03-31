package ru.netology.nework.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.data.repository.JobRepository
import ru.netology.nework.dto.Job
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class UserJobsViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _jobs = MutableLiveData<List<Job>>(emptyList())
    val jobs: LiveData<List<Job>> = _jobs

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isCreated = MutableLiveData(false)
    val isCreated: LiveData<Boolean> = _isCreated

    private val _isDeleted = MutableLiveData(false)
    val isDeleted: LiveData<Boolean> = _isDeleted

    private var currentUserId: Long = 0

    fun loadJobsForUser(userId: Long) {
        currentUserId = userId
        viewModelScope.launch {
            _isLoading.value = true
            try {
                jobRepository.getJobsForUser(userId).collect { jobList ->
                    _jobs.value = jobList
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки мест работы: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshJobs() {
        if (currentUserId != 0L) {
            viewModelScope.launch {
                _isRefreshing.value = true
                try {
                    jobRepository.refreshJobsForUser(currentUserId)
                } catch (e: Exception) {
                    _error.value = "Ошибка обновления мест работы: ${e.message}"
                } finally {
                    _isRefreshing.value = false
                }
            }
        }
    }

    fun createJob(company: String, position: String, startDate: Instant, endDate: Instant?) {
        if (currentUserId == 0L) return

        viewModelScope.launch {
            _isLoading.value = true
            _isCreated.value = false
            try {
                val job = Job(
                    id = 0,
                    userId = currentUserId,
                    company = company,
                    position = position,
                    startDate = startDate,
                    endDate = endDate
                )
                val result = jobRepository.createJob(job)
                if (result != null) {
                    _isCreated.value = true
                    refreshJobs()
                } else {
                    _error.value = "Ошибка создания места работы"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка создания места работы: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteJob(jobId: Long) {
        viewModelScope.launch {
            _isDeleted.value = false
            try {
                val success = jobRepository.deleteJob(jobId)
                if (success) {
                    _isDeleted.value = true
                    refreshJobs()
                } else {
                    _error.value = "Ошибка удаления места работы"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка удаления места работы: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearCreated() {
        _isCreated.value = false
    }

    fun clearDeleted() {
        _isDeleted.value = false
    }
}