package ru.netology.nework.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import ru.netology.nework.api.UsersApi
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.User
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.NetworkError
import ru.netology.nework.error.UnknownError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val usersApi: UsersApi
) {
    private val _users = MutableLiveData<List<User>>(emptyList())
    val users: LiveData<List<User>> = _users

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _jobs = MutableLiveData<List<Job>>(emptyList())
    val jobs: LiveData<List<Job>> = _jobs

    private val _wall = MutableLiveData<List<Post>>(emptyList())
    val wall: LiveData<List<Post>> = _wall

    init {
        loadUsers()
    }

    fun loadUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _loading.postValue(true)
                Log.d("UserRepository", "Loading users...")

                val response = usersApi.getUsers()

                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UserRepository", "Error: ${response.code()} - $errorBody")
                    _error.postValue("Ошибка загрузки: ${response.code()}")
                    _loading.postValue(false)
                    return@launch
                }

                val users = response.body()
                if (users != null) {
                    Log.d("UserRepository", "Users loaded: ${users.size}")
                    _users.postValue(users)
                    _error.postValue(null)
                } else {
                    Log.e("UserRepository", "Users list is null")
                    _error.postValue("Получен пустой список пользователей")
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Exception loading users: ${e.message}", e)
                _error.postValue("Неизвестная ошибка: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }

    suspend fun getUserWall(userId: Long): Result<List<Post>> = withContext(Dispatchers.IO) {
        try {
            // Временно возвращаем пустой список
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(NetworkError)
        }
    }

    suspend fun getUserJobs(userId: Long): Result<List<Job>> = withContext(Dispatchers.IO) {
        try {
            // Временно возвращаем пустой список
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(NetworkError)
        }
    }

    suspend fun saveJob(job: Job): Result<Job> = withContext(Dispatchers.IO) {
        try {
            // Временно возвращаем ту же работу
            Result.success(job)
        } catch (e: Exception) {
            Result.failure(NetworkError)
        }
    }

    suspend fun deleteJob(jobId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Временно возвращаем успех
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(NetworkError)
        }
    }

    suspend fun getUserById(id: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = usersApi.getUserById(id)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                return@withContext Result.failure(ApiError(response.code(), errorBody ?: "Unknown error"))
            }

            val user = response.body()
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(UnknownError)
            }
        } catch (e: Exception) {
            Result.failure(NetworkError)
        }
    }

    fun clearError() {
        _error.postValue(null)
    }
}