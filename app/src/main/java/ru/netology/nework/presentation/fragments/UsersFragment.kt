package ru.netology.nework.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentUsersBinding
import ru.netology.nework.dto.User
import ru.netology.nework.presentation.adapters.UserAdapter
import ru.netology.nework.presentation.viewmodels.UserViewModel

@AndroidEntryPoint
class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()

    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()

        userViewModel.loadUsers()
    }

    private fun initAdapter() {
        userAdapter = UserAdapter { user -> openUserDetail(user) }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
    }

    private fun setupObservers() {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            userAdapter.submitList(users)
            binding.emptyState.visibility = if (users.isEmpty()) View.VISIBLE else View.GONE
        }

        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        userViewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }

        userViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                userViewModel.clearError()
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            userViewModel.refreshUsers()
        }
    }

    private fun openUserDetail(user: User) {
        val bundle = Bundle().apply {
            putLong("userId", user.id)
        }
        findNavController().navigate(R.id.userDetailFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}