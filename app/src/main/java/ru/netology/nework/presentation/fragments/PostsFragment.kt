package ru.netology.nework.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentPostsBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.presentation.adapters.PostAdapter
import ru.netology.nework.presentation.viewmodels.AuthViewModel
import ru.netology.nework.presentation.viewmodels.PostViewModel

@AndroidEntryPoint
class PostsFragment : Fragment() {

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setupRecyclerView()
        setupObservers()
        setupListeners()
        setupSwipeRefresh()

        postViewModel.loadPosts()
    }

    private fun initAdapter() {
        postAdapter = PostAdapter(
            onItemClickListener = { post -> openPostDetail(post) },
            onLikeClickListener = { post -> postViewModel.likePost(post) },
            onMenuClickListener = { post, isAuthor -> if (isAuthor) showPostMenuDialog(post) },
            currentUserId = authViewModel.currentUserId.value
        )
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun setupObservers() {
        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.submitList(posts)
            binding.emptyState.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
        }

        postViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        postViewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }

        postViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                postViewModel.clearError()
            }
        }

        authViewModel.currentUserId.observe(viewLifecycleOwner) {
            initAdapter()
            binding.recyclerView.adapter = postAdapter
            postAdapter.submitList(postViewModel.posts.value)
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            if (authViewModel.isAuthenticated.value == true) {
                findNavController().navigate(R.id.createPostFragment)
            } else {
                showAuthDialog()
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            postViewModel.refreshPosts()
        }
    }

    private fun openPostDetail(post: Post) {
        val bundle = Bundle().apply {
            putLong("postId", post.id)
        }
        findNavController().navigate(R.id.postDetailFragment, bundle)
    }

    private fun showPostMenuDialog(post: Post) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Действия с постом")
            .setItems(arrayOf("Редактировать", "Удалить")) { _, which ->
                when (which) {
                    0 -> {
                        val bundle = Bundle().apply {
                            putLong("postId", post.id)
                        }
                        findNavController().navigate(R.id.createPostFragment, bundle)
                    }
                    1 -> {
                        showDeleteConfirmationDialog(post.id)
                    }
                }
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(postId: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удаление поста")
            .setMessage("Вы уверены, что хотите удалить этот пост?")
            .setPositiveButton("Удалить") { _, _ ->
                postViewModel.deletePost(postId)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showAuthDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Требуется авторизация")
            .setMessage("Для создания поста необходимо войти в аккаунт")
            .setPositiveButton("Войти") { _, _ ->
                findNavController().navigate(R.id.loginFragment)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}