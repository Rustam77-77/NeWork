package ru.netology.nework.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentUserWallBinding
import ru.netology.nework.presentation.adapters.PostAdapter
import ru.netology.nework.presentation.viewmodels.AuthViewModel
import ru.netology.nework.presentation.viewmodels.UserWallViewModel

@AndroidEntryPoint
class UserWallFragment : Fragment() {

    private var _binding: FragmentUserWallBinding? = null
    private val binding get() = _binding!!

    private val wallViewModel: UserWallViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var postAdapter: PostAdapter
    private var userId: Long = 0

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

        arguments?.let {
            userId = it.getLong("userId", 0)
        }

        initAdapter()
        setupRecyclerView()
        setupObservers()

        wallViewModel.loadUserPosts(userId)
    }

    private fun initAdapter() {
        postAdapter = PostAdapter(
            onItemClickListener = { /* Не переходим в детали на стене */ },
            onLikeClickListener = { post -> wallViewModel.likePost(post) },
            onMenuClickListener = { _, _ -> },
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
        wallViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.submitList(posts)
            binding.emptyState.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
        }

        wallViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        wallViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                wallViewModel.clearError()
            }
        }
    }

    companion object {
        fun newInstance(userId: Long): UserWallFragment {
            val fragment = UserWallFragment()
            val args = Bundle()
            args.putLong("userId", userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}