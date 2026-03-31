package ru.netology.nework.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.data.database.dao.UserDao
import ru.netology.nework.data.database.entities.toEntity
import ru.netology.nework.data.database.entities.toModel
import ru.netology.nework.dto.User
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "UserRepository"

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val apiService: ApiService
) {
    fun getAllUsers(): Flow<List<User>> =
        userDao.getAllUsers().map { entities ->
            Log.d(TAG, "getAllUsers: returning ${entities.size} entities from DB")
            entities.map { it.toModel() }
        }

    fun getUserById(userId: Long): Flow<User?> =
        userDao.getUserById(userId).map { entity ->
            Log.d(TAG, "getUserById: userId=$userId, found=${entity != null}")
            entity?.toModel()
        }

    suspend fun refreshUsers() {
        try {
            Log.d(TAG, "refreshUsers: starting network request")
            val response = apiService.getAllUsers()
            Log.d(TAG, "refreshUsers: response code = ${response.code()}")

            if (response.isSuccessful) {
                response.body()?.let { users ->
                    Log.d(TAG, "refreshUsers: received ${users.size} users from server")
                    userDao.insertAll(users.map { it.toEntity() })
                    Log.d(TAG, "refreshUsers: saved to DB")
                } ?: run {
                    Log.e(TAG, "refreshUsers: response body is null")
                }
            } else {
                Log.e(TAG, "refreshUsers: error response ${response.code()}, message: ${response.message()}")
                response.errorBody()?.let {
                    Log.e(TAG, "refreshUsers: error body = ${it.string()}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "refreshUsers: exception", e)
        }
    }

    suspend fun refreshUserById(userId: Long) {
        try {
            Log.d(TAG, "refreshUserById: userId=$userId")
            val response = apiService.getUserById(userId)
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    Log.d(TAG, "refreshUserById: received user")
                    userDao.insert(user.toEntity())
                }
            } else {
                Log.e(TAG, "refreshUserById: error ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "refreshUserById: exception", e)
        }
    }
}