package ru.netology.nework.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentCreatePostBinding
import ru.netology.nework.presentation.adapters.UserSelectionAdapter
import ru.netology.nework.presentation.viewmodels.AuthViewModel
import ru.netology.nework.presentation.viewmodels.PostViewModel
import ru.netology.nework.presentation.viewmodels.UserViewModel

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var selectedUsers: MutableList<Long> = mutableListOf()
    private var postId: Long = 0
    private var isEditMode: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            postId = it.getLong("postId", 0)
            isEditMode = postId != 0L
        }

        setupMenu()
        setupObservers()
        setupListeners()

        if (isEditMode) {
            postViewModel.loadPostById(postId)
        } else {
            userViewModel.loadUsers()
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.save_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        savePost()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupObservers() {
        postViewModel.post.observe(viewLifecycleOwner) { post ->
            post?.let {
                binding.postContent.setText(it.content)
                selectedUsers.addAll(it.mentionIds)
                updateSelectedUsersCount()
            }
        }

        postViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        postViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                postViewModel.clearError()
            }
        }

        postViewModel.isCreated.observe(viewLifecycleOwner) { isCreated ->
            if (isCreated) {
                findNavController().popBackStack()
                postViewModel.clearCreated()
            }
        }

        userViewModel.users.observe(viewLifecycleOwner) { users ->
            if (users.isNotEmpty()) {
                // Пользователи загружены
            }
        }
    }

    private fun setupListeners() {
        binding.selectUsersButton.setOnClickListener {
            showUserSelectionDialog()
        }
    }

    private fun showUserSelectionDialog() {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            val adapter = UserSelectionAdapter(
                users = users,
                selectedUserIds = selectedUsers,
                onUserSelected = { userId, isChecked ->
                    if (isChecked) {
                        selectedUsers.add(userId)
                    } else {
                        selectedUsers.remove(userId)
                    }
                    updateSelectedUsersCount()
                }
            )

            val dialogView = layoutInflater.inflate(R.layout.dialog_user_selection, null)
            val recyclerView = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Выберите упомянутых пользователей")
                .setView(dialogView)
                .setPositiveButton("ОК") { _, _ ->
                    updateSelectedUsersCount()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    private fun updateSelectedUsersCount() {
        val count = selectedUsers.size
        binding.selectedUsersCount.text = if (count > 0) {
            "Выбрано пользователей: $count"
        } else {
            "Никто не выбран"
        }
    }

    private fun savePost() {
        val content = binding.postContent.text.toString()
        if (content.isBlank()) {
            Snackbar.make(binding.root, "Введите текст поста", Snackbar.LENGTH_LONG).show()
            return
        }

        val currentUserId = authViewModel.currentUserId.value
        val currentUserName = "Пользователь"

        if (currentUserId == null) {
            Snackbar.make(binding.root, "Необходимо авторизоваться", Snackbar.LENGTH_LONG).show()
            return
        }

        if (isEditMode) {
            postViewModel.updatePost(postId, content, selectedUsers)
        } else {
            postViewModel.createPost(content, selectedUsers, currentUserId, currentUserName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}