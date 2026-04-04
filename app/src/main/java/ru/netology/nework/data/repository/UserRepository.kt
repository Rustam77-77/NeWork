package ru.netology.nework.data.repository

import kotlinx.coroutines.flow.firstOrNull
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

    suspend fun getAllUsers(): List<User> {
        return try {
            val response = apiService.getAllUsers()
            if (response.isSuccessful) {
                response.body()?.let { users ->
                    userDao.insertAll(users.map { it.toEntity() })
                    users
                } ?: emptyList()
            } else {
                val usersFlow = userDao.getAll()
                usersFlow.firstOrNull()?.map { it.toModel() } ?: emptyList()
            }
        } catch (e: Exception) {
            val usersFlow = userDao.getAll()
            usersFlow.firstOrNull()?.map { it.toModel() } ?: emptyList()
        }
    }

    suspend fun getUserById(id: Long): User? {
        return try {
            val response = apiService.getUserById(id)
            if (response.isSuccessful) {
                response.body()?.also { user ->
                    userDao.insert(user.toEntity())
                }
            } else {
                userDao.getById(id)?.toModel()
            }
        } catch (e: Exception) {
            userDao.getById(id)?.toModel()
        }
    }

    suspend fun refreshUsers() {
        try {
            val response = apiService.getAllUsers()
            if (response.isSuccessful) {
                response.body()?.let { users ->
                    userDao.insertAll(users.map { it.toEntity() })
                }
            }
        } catch (e: Exception) {
            // Обработка ошибки
        }
    }
}