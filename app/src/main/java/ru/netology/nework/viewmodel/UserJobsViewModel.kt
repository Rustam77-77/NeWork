package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.netology.nework.dto.Job
import ru.netology.nework.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserJobsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _jobs = MutableLiveData<List<Job>>()
    val jobs: LiveData<List<Job>> = _jobs

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadUserJobs(userId: Long) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = userRepository.getUserJobs(userId)
                _jobs.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки работ: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun saveJob(userId: Long, job: Job) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = userRepository.saveJob(userId, job)
                if (result != null) {
                    loadUserJobs(userId)
                } else {
                    _error.value = "Ошибка при сохранении работы"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteJob(userId: Long, jobId: Long) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = userRepository.deleteJob(userId, jobId)
                if (result) {
                    loadUserJobs(userId)
                } else {
                    _error.value = "Ошибка при удалении работы"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}