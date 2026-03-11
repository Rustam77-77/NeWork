package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.nework.dto.User
import ru.netology.nework.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val users: LiveData<List<User>> = userRepository.users
    val loading: LiveData<Boolean> = userRepository.loading
    val error: LiveData<String?> = userRepository.error

    fun loadUsers() {
        userRepository.loadUsers()
    }
}