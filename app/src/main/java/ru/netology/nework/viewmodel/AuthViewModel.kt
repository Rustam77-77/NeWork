package ru.netology.nework.viewmodel

<<<<<<< HEAD
=======
import android.util.Log
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
<<<<<<< HEAD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import ru.netology.nework.error.AppError
import ru.netology.nework.repository.AuthRepository
=======
import ru.netology.nework.error.AppError
import ru.netology.nework.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    private val _currentUserId = MutableLiveData<Long?>()
    val currentUserId: LiveData<Long?> = _currentUserId

    private val _currentUserLogin = MutableLiveData<String?>()
    val currentUserLogin: LiveData<String?> = _currentUserLogin

    private val _currentUserName = MutableLiveData<String?>()
    val currentUserName: LiveData<String?> = _currentUserName

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        viewModelScope.launch {
            authRepository.isAuthenticated.collect { isAuth ->
                _isAuthenticated.postValue(isAuth)
            }
        }

        viewModelScope.launch {
            authRepository.currentUserId.collect { userId ->
                _currentUserId.postValue(userId)
            }
        }

        viewModelScope.launch {
            authRepository.currentUserLogin.collect { login ->
                _currentUserLogin.postValue(login)
            }
        }

        viewModelScope.launch {
            authRepository.currentUserName.collect { name ->
                _currentUserName.postValue(name)
            }
        }
    }

    fun authenticate(login: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
<<<<<<< HEAD
            try {
                authRepository.authenticate(login, password)
                _authState.value = AuthState.Success
            } catch (e: AppError.ApiError) {
                _authState.value = AuthState.Error
                _error.value = when (e.code) {
                    400 -> "Неправильный логин или пароль"
                    else -> "Ошибка сервера: ${e.message}"
                }
            } catch (e: AppError.NetworkError) {
                _authState.value = AuthState.Error
                _error.value = "Ошибка сети. Проверьте подключение"
            } catch (e: Exception) {
                _authState.value = AuthState.Error
                _error.value = "Произошла неизвестная ошибка"
=======
            _error.value = null

            Log.d("AuthViewModel", "Starting authentication for login: $login")

            try {
                val token = authRepository.authenticate(login, password)
                Log.d("AuthViewModel", "Authentication successful, token: $token")
                _authState.value = AuthState.Success
            } catch (e: AppError.ApiError) {
                Log.e("AuthViewModel", "API Error: ${e.code} - ${e.message}")
                _authState.value = AuthState.Error
                _error.value = when (e.code) {
                    400 -> "Неправильный логин или пароль"
                    403 -> "Доступ запрещен. Проверьте API ключ"
                    else -> "Ошибка сервера: ${e.message}"
                }
            } catch (e: AppError.NetworkError) {
                Log.e("AuthViewModel", "Network Error", e)
                _authState.value = AuthState.Error
                _error.value = "Ошибка сети. Проверьте подключение"
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Unexpected error", e)
                _authState.value = AuthState.Error
                _error.value = "Произошла неизвестная ошибка: ${e.message}"
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            }
        }
    }

    fun register(login: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
<<<<<<< HEAD
            try {
                authRepository.register(login, password, name)
                _authState.value = AuthState.Success
            } catch (e: AppError.ApiError) {
                _authState.value = AuthState.Error
                _error.value = when (e.code) {
                    400 -> "Пользователь с таким логином уже зарегистрирован"
                    else -> "Ошибка сервера: ${e.message}"
                }
            } catch (e: AppError.NetworkError) {
                _authState.value = AuthState.Error
                _error.value = "Ошибка сети. Проверьте подключение"
            } catch (e: Exception) {
                _authState.value = AuthState.Error
                _error.value = "Произошла неизвестная ошибка"
=======
            _error.value = null

            Log.d("AuthViewModel", "Starting registration for login: $login")

            try {
                val token = authRepository.register(login, password, name)
                Log.d("AuthViewModel", "Registration successful, token: $token")
                _authState.value = AuthState.Success
            } catch (e: AppError.ApiError) {
                Log.e("AuthViewModel", "API Error: ${e.code} - ${e.message}")
                _authState.value = AuthState.Error
                _error.value = when (e.code) {
                    400 -> "Пользователь с таким логином уже зарегистрирован"
                    403 -> "Доступ запрещен. Проверьте API ключ"
                    else -> "Ошибка сервера: ${e.message}"
                }
            } catch (e: AppError.NetworkError) {
                Log.e("AuthViewModel", "Network Error", e)
                _authState.value = AuthState.Error
                _error.value = "Ошибка сети. Проверьте подключение"
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Unexpected error", e)
                _authState.value = AuthState.Error
                _error.value = "Произошла неизвестная ошибка: ${e.message}"
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            }
        }
    }

    fun registerWithAvatar(login: String, password: String, name: String, avatarPart: MultipartBody.Part) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
<<<<<<< HEAD
            try {
                authRepository.registerWithAvatar(login, password, name, avatarPart)
                _authState.value = AuthState.Success
            } catch (e: AppError.ApiError) {
                _authState.value = AuthState.Error
                _error.value = when (e.code) {
                    400 -> "Пользователь с таким логином уже зарегистрирован"
                    else -> "Ошибка сервера: ${e.message}"
                }
            } catch (e: AppError.NetworkError) {
                _authState.value = AuthState.Error
                _error.value = "Ошибка сети. Проверьте подключение"
            } catch (e: Exception) {
                _authState.value = AuthState.Error
                _error.value = "Произошла неизвестная ошибка"
=======
            _error.value = null

            Log.d("AuthViewModel", "Starting registration with avatar for login: $login")

            try {
                val token = authRepository.registerWithAvatar(login, password, name, avatarPart)
                Log.d("AuthViewModel", "Registration with avatar successful, token: $token")
                _authState.value = AuthState.Success
            } catch (e: AppError.ApiError) {
                Log.e("AuthViewModel", "API Error: ${e.code} - ${e.message}")
                _authState.value = AuthState.Error
                _error.value = when (e.code) {
                    400 -> "Пользователь с таким логином уже зарегистрирован"
                    403 -> "Доступ запрещен. Проверьте API ключ"
                    else -> "Ошибка сервера: ${e.message}"
                }
            } catch (e: AppError.NetworkError) {
                Log.e("AuthViewModel", "Network Error", e)
                _authState.value = AuthState.Error
                _error.value = "Ошибка сети. Проверьте подключение"
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Unexpected error", e)
                _authState.value = AuthState.Error
                _error.value = "Произошла неизвестная ошибка: ${e.message}"
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
<<<<<<< HEAD
=======
            _authState.value = AuthState.Idle
            Log.d("AuthViewModel", "User logged out")
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
        }
    }

    fun clearError() {
        _error.value = null
        _authState.value = AuthState.Idle
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        object Error : AuthState()
    }
}