package ru.netology.nework.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentUserWallBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.presentation.adapters.PostAdapter
import ru.netology.nework.presentation.viewmodels.PostViewModel
import ru.netology.nework.presentation.viewmodels.UserWallViewModel

@AndroidEntryPoint
class UserWallFragment : Fragment() {

    private var _binding: FragmentUserWallBinding? = null
    private val binding get() = _binding!!

    private val userWallViewModel: UserWallViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    private lateinit var postAdapter: PostAdapter
    private var userId: Long = 0

    companion object {
        fun newInstance(userId: Long): UserWallFragment {
            val fragment = UserWallFragment()
            val args = Bundle()
            args.putLong("userId", userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserWallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        arguments?.let {
            userId = it.getLong("userId", 0)
        }

        setupRecyclerView()
        setupObservers()

        if (userId != 0L) {
            userWallViewModel.loadUserPosts(userId)
        }
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Посты пользователя"
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            onItemClickListener = { post -> openPostDetail(post) },
            onLikeClickListener = { post -> postViewModel.likePost(post) },
            onMenuClickListener = { _, _ -> },
            currentUserId = null
        )
        binding.postsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun setupObservers() {
        userWallViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.submitList(posts)
            binding.postsRecyclerView.isVisible = posts.isNotEmpty()
            binding.emptyText.isVisible = posts.isEmpty()
        }

        userWallViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        userWallViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                userWallViewModel.clearError()
            }
        }
    }

    private fun openPostDetail(post: Post) {
        // Используем общую навигацию к PostDetailFragment
        findNavController().navigate(
            R.id.postDetailFragment,
            Bundle().apply { putLong("postId", post.id) }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}