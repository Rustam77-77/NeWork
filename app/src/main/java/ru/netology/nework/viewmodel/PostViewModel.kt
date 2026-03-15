package ru.netology.nework.viewmodel
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.repository.PostRepository
import ru.netology.nework.util.SingleLiveEvent
import javax.inject.Inject
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {
    val postCreated = SingleLiveEvent<Unit>()
    private val _postContent = MutableLiveData<String>("")
    fun changeContent(content: String) {
        _postContent.value = content
    }
    fun save() = viewModelScope.launch {
        try {
            // repository.save(_postContent.value ?: "") // Раскомментируйте, если метод в репозитории готов
            postCreated.value = Unit
        } catch (e: Exception) {
            // Ошибка сохранения
        }
    }
}