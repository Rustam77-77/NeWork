package ru.netology.nework.viewmodel
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Post
import ru.netology.nework.repository.UserRepository
import javax.inject.Inject
@HiltViewModel
class UserWallViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error
    fun loadUserWall(userId: Long) = viewModelScope.launch {
        try {
            // Репозиторий должен возвращать List<Post>
            _posts.value = repository.getUserWall(userId)
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
}