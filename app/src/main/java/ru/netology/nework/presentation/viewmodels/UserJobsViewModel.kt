package ru.netology.nework.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.data.repository.JobRepository
import ru.netology.nework.dto.Job
import javax.inject.Inject

@HiltViewModel
class UserJobsViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _jobs = MutableLiveData<List<Job>>(emptyList())
    val jobs: LiveData<List<Job>> = _jobs

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadUserJobs(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val jobsList = jobRepository.getJobsForUser(userId)
                _jobs.value = jobsList
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshJobsForUser(userId: Long) {
        viewModelScope.launch {
            try {
                jobRepository.refreshJobs(userId)
                val jobsList = jobRepository.getJobsForUser(userId)
                _jobs.value = jobsList
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun createJob(
        userId: Long,
        name: String,
        position: String,
        start: java.time.Instant,
        finish: java.time.Instant? = null,
        link: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val job = Job(
                    id = 0,
                    userId = userId,
                    name = name,
                    position = position,
                    start = start,
                    finish = finish,
                    link = link
                )
                val result = jobRepository.createJob(job)
                if (result != null) {
                    loadUserJobs(userId)
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteJob(jobId: Long, userId: Long) {
        viewModelScope.launch {
            try {
                jobRepository.deleteJob(jobId)
                loadUserJobs(userId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}