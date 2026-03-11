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
<<<<<<< HEAD
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
=======
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
import ru.netology.nework.databinding.FragmentUsersBinding
import ru.netology.nework.adapter.UserAdapter
import ru.netology.nework.dto.User
import ru.netology.nework.viewmodel.UserViewModel
<<<<<<< HEAD
=======
import dagger.hilt.android.AndroidEntryPoint
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399

@AndroidEntryPoint
class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

<<<<<<< HEAD
    private val viewModel: UserViewModel by viewModels()
=======
    private val userViewModel: UserViewModel by viewModels()

>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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

<<<<<<< HEAD
        viewModel.loadUsers()
=======
        userViewModel.loadUsers()
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
<<<<<<< HEAD
            onItemClick = { user ->
=======
            onItemClickListener = { user ->
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                openUserDetail(user)
            }
        )

        binding.recyclerViewUsers.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
<<<<<<< HEAD
            viewModel.loadUsers()
=======
            userViewModel.loadUsers()
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
        }
    }

    private fun setupObservers() {
<<<<<<< HEAD
        viewModel.users.observe(viewLifecycleOwner) { users ->
=======
        userViewModel.users.observe(viewLifecycleOwner) { users ->
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            userAdapter.submitList(users)
            binding.swipeRefreshLayout.isRefreshing = false

            if (users.isNullOrEmpty()) {
                binding.textViewEmpty.visibility = View.VISIBLE
            } else {
                binding.textViewEmpty.visibility = View.GONE
            }
        }

<<<<<<< HEAD
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true && viewModel.users.value.isNullOrEmpty())
                View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
=======
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
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            }
        }
    }

    private fun openUserDetail(user: User) {
        val bundle = Bundle().apply {
            putSerializable("user", user)
        }
<<<<<<< HEAD
        findNavController().navigate(R.id.action_usersFragment_to_userDetailFragment, bundle)
=======
        findNavController().navigate(ru.netology.nework.R.id.action_usersFragment_to_userDetailFragment, bundle)
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}