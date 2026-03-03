package ru.netology.nework.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nework.databinding.FragmentUserWallBinding
import ru.netology.nework.adapter.PostAdapter
import ru.netology.nework.viewmodel.UserWallViewModel
import dagger.hilt.android.AndroidEntryPoint

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
        }
        userId?.let { viewModel.loadUserWall(it) }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            onLikeClickListener = { /* TODO */ },
            onMenuClickListener = { /* TODO */ },
            onItemClickListener = { /* TODO */ }
        )

        binding.recyclerViewWall.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.submitList(posts)
            if (posts.isNullOrEmpty()) {
                binding.textViewEmpty.visibility = View.VISIBLE
            } else {
                binding.textViewEmpty.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}