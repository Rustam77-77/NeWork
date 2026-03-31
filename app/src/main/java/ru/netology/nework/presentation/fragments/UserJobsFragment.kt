package ru.netology.nework.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentUserJobsBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.presentation.adapters.JobAdapter
import ru.netology.nework.presentation.viewmodels.UserJobsViewModel

@AndroidEntryPoint
class UserJobsFragment : Fragment() {

    private var _binding: FragmentUserJobsBinding? = null
    private val binding get() = _binding!!

    private val jobsViewModel: UserJobsViewModel by viewModels()

    private lateinit var jobAdapter: JobAdapter
    private var userId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserJobsBinding.inflate(inflater, container, false)
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

        jobsViewModel.loadJobsForUser(userId)
    }

    private fun initAdapter() {
        jobAdapter = JobAdapter { job -> showDeleteJobDialog(job) }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = jobAdapter
        }
    }

    private fun setupObservers() {
        jobsViewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            jobAdapter.submitList(jobs)
            binding.emptyState.isVisible = jobs.isEmpty()
            binding.progressBar.isVisible = false
        }

        jobsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        jobsViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                jobsViewModel.clearError()
            }
        }

        jobsViewModel.isDeleted.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                jobsViewModel.refreshJobs()
                jobsViewModel.clearDeleted()
            }
        }
    }

    private fun showDeleteJobDialog(job: Job) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удаление места работы")
            .setMessage("Вы уверены, что хотите удалить это место работы?")
            .setPositiveButton("Удалить") { _, _ ->
                jobsViewModel.deleteJob(job.id)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    companion object {
        fun newInstance(userId: Long): UserJobsFragment {
            val fragment = UserJobsFragment()
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