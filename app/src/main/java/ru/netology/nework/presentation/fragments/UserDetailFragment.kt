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
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentUserDetailBinding
import ru.netology.nework.presentation.adapters.JobAdapter
import ru.netology.nework.presentation.viewmodels.UserJobsViewModel
import ru.netology.nework.presentation.viewmodels.UserViewModel
import ru.netology.nework.presentation.viewmodels.UserWallViewModel

@AndroidEntryPoint
class UserDetailFragment : Fragment() {

    private var _binding: FragmentUserDetailBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()
    private val userWallViewModel: UserWallViewModel by viewModels()
    private val userJobsViewModel: UserJobsViewModel by viewModels()

    private lateinit var jobAdapter: JobAdapter
    private var userId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailBinding.inflate(inflater, container, false)
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

        userViewModel.loadUserById(userId)
        userJobsViewModel.loadUserJobs(userId)
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Профиль пользователя"
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        jobAdapter = JobAdapter { job ->
            // Обработка клика по работе
        }
        binding.jobsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = jobAdapter
        }
    }

    private fun setupObservers() {
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                displayUser(it)
            }
        }

        userJobsViewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            jobAdapter.submitList(jobs)
            binding.jobsRecyclerView.isVisible = jobs.isNotEmpty()
            binding.noJobsText.isVisible = jobs.isEmpty()
        }

        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        userViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                userViewModel.clearError()
            }
        }
    }

    private fun displayUser(user: ru.netology.nework.dto.User) {
        binding.apply {
            userName.text = user.name
            userLogin.text = "@${user.login}"

            // Загрузка аватара, если есть URL
            user.avatar?.let { avatarUrl ->
                Glide.with(root.context)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(userAvatar)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}