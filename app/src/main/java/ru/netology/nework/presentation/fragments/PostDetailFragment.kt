package ru.netology.nework.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentPostDetailBinding
import ru.netology.nework.presentation.viewmodels.PostViewModel
import ru.netology.nework.presentation.viewmodels.UserViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var postId: Long = 0

    private val dateFormatter = DateTimeFormatter
        .ofPattern("dd.MM.yyyy HH:mm")
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            postId = it.getLong("postId", 0)
        }

        setupObservers()
        postViewModel.loadPostById(postId)
        userViewModel.loadUsers()
    }

    private fun setupObservers() {
        postViewModel.post.observe(viewLifecycleOwner) { post ->
            post?.let {
                displayPost(it)
            }
        }

        postViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        postViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                postViewModel.clearError()
            }
        }
    }

    private fun displayPost(post: ru.netology.nework.dto.Post) {
        binding.apply {
            authorName.text = post.author
            date.text = dateFormatter.format(post.published)
            content.text = post.content
            authorJob.text = post.authorJob ?: "В поиске работы"

            if (post.attachment?.url.isNullOrEmpty()) {
                linkContainer.isVisible = false
            } else {
                linkContainer.isVisible = true
                linkText.text = post.attachment?.url
                linkContainer.setOnClickListener {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(post.attachment?.url))
                    startActivity(intent)
                }
            }

            if (post.mentionIds.isEmpty()) {
                mentionedUsersTitle.isVisible = false
                mentionedUsersList.isVisible = false
            } else {
                mentionedUsersTitle.isVisible = true
                mentionedUsersList.isVisible = true
                loadMentionedUsers(post.mentionIds)
            }
        }
    }

    private fun loadMentionedUsers(userIds: List<Long>) {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            val mentionedUsers = users.filter { it.id in userIds }
            val names = mentionedUsers.joinToString(", ") { it.name }
            binding.mentionedUsersList.text = names
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}