package ru.netology.nework.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.netology.nework.databinding.FragmentUsersBinding
import ru.netology.nework.presentation.adapters.UserAdapter
import ru.netology.nework.presentation.viewmodels.UserViewModel

@AndroidEntryPoint
class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()

    private lateinit var userAdapter: UserAdapter

    companion object {
        private const val TAG = "UsersFragment"
    }

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

        Log.d(TAG, "onViewCreated started")

        initAdapter()
        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()

        // Загружаем данные
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.loadUsers()
            userViewModel.refreshUsers()
        }
    }

    private fun initAdapter() {
        userAdapter = UserAdapter { user ->
            Log.d(TAG, "User clicked: ${user.id}, name=${user.name}")
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
    }

    private fun setupObservers() {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            Log.d(TAG, "Users received: ${users.size}")
            userAdapter.submitList(users)
            binding.emptyState.isVisible = users.isEmpty()
            binding.progressBar.isVisible = false
        }

        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        userViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e(TAG, "Error: $it")
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                userViewModel.clearError()
                binding.progressBar.isVisible = false
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            userViewModel.refreshUsers()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}