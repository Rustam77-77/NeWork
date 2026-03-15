package ru.netology.nework.viewmodel
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.AuthState
import ru.netology.nework.repository.AuthRepository
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _authorized = MutableLiveData<AuthState?>(null)
    val authorized: LiveData<AuthState?> = _authorized
    fun login(login: String, pass: String) = viewModelScope.launch {
        try {
            _authorized.value = repository.authenticate(login, pass)
        } catch (e: Exception) {
            // Обработка ошибки
        }
    }
    // ДОБАВЛЯЕМ ЭТОТ МЕТОД
    fun register(login: String, pass: String, name: String) = viewModelScope.launch {
        try {
            _authorized.value = repository.register(login, pass, name)
        } catch (e: Exception) {
            // Обработка ошибки
        }
    }
}