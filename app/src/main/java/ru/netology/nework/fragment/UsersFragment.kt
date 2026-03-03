package ru.netology.nework.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nework.databinding.FragmentUsersBinding
import ru.netology.nework.adapter.UserAdapter
import ru.netology.nework.dto.User
import ru.netology.nework.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

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

        setupRecyclerView()
        setupObservers()

        userViewModel.loadUsers()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            onItemClickListener = { user ->
                openUserDetail(user)
            }
        )

        binding.recyclerViewUsers.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            userViewModel.loadUsers()
        }
    }

    private fun setupObservers() {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            userAdapter.submitList(users)
            binding.swipeRefreshLayout.isRefreshing = false

            if (users.isNullOrEmpty()) {
                binding.textViewEmpty.visibility = View.VISIBLE
            } else {
                binding.textViewEmpty.visibility = View.GONE
            }
        }

        userViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading == true && userViewModel.users.value.isNullOrEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        userViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                userViewModel.clearError()
            }
        }
    }

    private fun openUserDetail(user: User) {
        val bundle = Bundle().apply {
            putSerializable("user", user)
        }
        findNavController().navigate(ru.netology.nework.R.id.action_usersFragment_to_userDetailFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}