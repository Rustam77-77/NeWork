package ru.netology.nework.repository
import ru.netology.nework.api.ApiService
import ru.netology.nework.dto.AuthState
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun authenticate(login: String, pass: String): AuthState {
        val response = apiService.updateUser(login, pass)
        return response.body() ?: throw Exception("Error")
    }
    suspend fun register(login: String, pass: String, name: String): AuthState {
        // Теперь это скомпилируется
        val response = apiService.registerUser(login, pass, name)
        return response.body() ?: throw Exception("Error")
    }
}