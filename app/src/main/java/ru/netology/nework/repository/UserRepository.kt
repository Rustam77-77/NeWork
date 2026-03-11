package ru.netology.nework.repository

import ru.netology.nework.api.UsersApi
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val usersApi: UsersApi
) {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    suspend fun loadUsers() {
        _loading.value = true
        try {
            val response = usersApi.getAll()
            if (response.isSuccessful) {
                _users.value = response.body() ?: emptyList()
                _error.value = null
            } else {
                _error.value = "Ошибка загрузки пользователей: ${response.code()}"
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети. Проверьте подключение к интернету"
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
        } finally {
            _loading.value = false
        }
    }

    suspend fun getUserWall(userId: Long): List<Post> {
        return try {
            val response = usersApi.getWall(userId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                _error.value = "Ошибка загрузки стены: ${response.code()}"
                emptyList()
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при загрузке стены"
            emptyList()
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            emptyList()
        }
    }

    suspend fun getUserJobs(userId: Long): List<Job> {
        return try {
            val response = usersApi.getJobs(userId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                _error.value = "Ошибка загрузки работ: ${response.code()}"
                emptyList()
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при загрузке работ"
            emptyList()
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            emptyList()
        }
    }

    suspend fun saveJob(userId: Long, job: Job): Job? {
        return try {
            val response = usersApi.saveJob(userId, job)
            if (response.isSuccessful) {
                response.body()
            } else {
                _error.value = "Ошибка при сохранении работы: ${response.code()}"
                null
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при сохранении работы"
            null
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            null
        }
    }

    suspend fun deleteJob(userId: Long, jobId: Long): Boolean {
        return try {
            val response = usersApi.removeJob(userId, jobId)
            if (response.isSuccessful) {
                true
            } else {
                _error.value = "Ошибка при удалении работы: ${response.code()}"
                false
            }
        } catch (e: IOException) {
            _error.value = "Ошибка сети при удалении работы"
            false
        } catch (e: Exception) {
            _error.value = "Неизвестная ошибка: ${e.message}"
            false
        }
    }

    fun clearError() {
        _error.value = null
    }
}