package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Job
import ru.netology.nework.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserJobsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _jobs = MutableLiveData<List<Job>>(emptyList())
    val jobs: LiveData<List<Job>> = _jobs

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadJobs(userId: Long) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val result = userRepository.getUserJobs(userId)
                result.onSuccess { jobs ->
                    _jobs.value = jobs
                    _error.value = null
                }.onFailure { exception ->
                    _error.value = exception.message
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun saveJob(job: Job) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val result = userRepository.saveJob(job)
                result.onSuccess { updatedJob ->
                    val currentList = _jobs.value?.toMutableList() ?: mutableListOf()
                    val index = currentList.indexOfFirst { it.id == updatedJob.id }
                    if (index != -1) {
                        currentList[index] = updatedJob
                    } else {
                        currentList.add(updatedJob)
                    }
                    _jobs.value = currentList
                    _error.value = null
                }.onFailure { exception ->
                    _error.value = exception.message
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteJob(job: Job) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val result = userRepository.deleteJob(job.id)
                result.onSuccess {
                    val currentList = _jobs.value?.toMutableList() ?: mutableListOf()
                    currentList.removeAll { it.id == job.id }
                    _jobs.value = currentList
                    _error.value = null
                }.onFailure { exception ->
                    _error.value = exception.message
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}