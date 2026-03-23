package ru.netology.nework.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.data.database.dao.UserDao
import ru.netology.nework.data.database.entities.toEntity
import ru.netology.nework.data.database.entities.toModel
import ru.netology.nework.dto.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val apiService: ApiService
) {
    fun getAllUsers(): Flow<List<User>> =
        userDao.getAllUsers().map { entities -> entities.map { it.toModel() } }

    fun getUserById(userId: Long): Flow<User?> =
        userDao.getUserById(userId).map { entity -> entity?.toModel() }

    suspend fun refreshUsers() {
        try {
            val response = apiService.getAllUsers()
            if (response.isSuccessful) {
                response.body()?.let { users ->
                    userDao.insertAll(users.map { it.toEntity() })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun refreshUserById(userId: Long) {
        try {
            val response = apiService.getUserById(userId)
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    userDao.insert(user.toEntity())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}