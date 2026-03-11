package ru.netology.nework.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentUserWallBinding
import ru.netology.nework.adapter.PostAdapter
import ru.netology.nework.viewmodel.UserWallViewModel

@AndroidEntryPoint
class UserWallFragment : Fragment() {

    private var _binding: FragmentUserWallBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserWallViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter

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

        setupRecyclerView()
        setupObservers()

        val userId = (parentFragment as? UserDetailFragment)?.arguments?.getSerializable("user")?.let {
            (it as ru.netology.nework.dto.User).id
        } ?: 0
        viewModel.loadUserWall(userId)
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            onItemClick = { post ->
                Toast.makeText(requireContext(), "Пост: ${post.id}", Toast.LENGTH_SHORT).show()
            },
            onLikeClick = { post ->
                Toast.makeText(requireContext(), "Лайк: ${post.id}", Toast.LENGTH_SHORT).show()
            },
            onMenuClick = { post ->
                Toast.makeText(requireContext(), "Меню: ${post.id}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerViewWall.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.submitList(posts)
            binding.textViewEmpty.visibility = if (posts.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}