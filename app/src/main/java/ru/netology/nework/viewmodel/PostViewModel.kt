package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.nework.dto.Post
import ru.netology.nework.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    val posts: LiveData<List<Post>> = postRepository.posts
    val loading: LiveData<Boolean> = postRepository.loading
    val error: LiveData<String?> = postRepository.error

    fun loadPosts() {
        postRepository.loadPosts()
    }
}