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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentUserJobsBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.presentation.adapters.JobAdapter
import ru.netology.nework.presentation.viewmodels.UserJobsViewModel
import java.time.Instant

@AndroidEntryPoint
class UserJobsFragment : Fragment() {

    private var _binding: FragmentUserJobsBinding? = null
    private val binding get() = _binding!!

    private val userJobsViewModel: UserJobsViewModel by viewModels()

    private lateinit var jobAdapter: JobAdapter
    private var userId: Long = 0

    companion object {
        fun newInstance(userId: Long): UserJobsFragment {
            val fragment = UserJobsFragment()
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
        _binding = FragmentUserJobsBinding.inflate(inflater, container, false)
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
        setupListeners()

        if (userId != 0L) {
            userJobsViewModel.loadUserJobs(userId)
        }
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Места работы"
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        jobAdapter = JobAdapter { job ->
            showJobMenuDialog(job)
        }
        binding.jobsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = jobAdapter
        }
    }

    private fun setupObservers() {
        userJobsViewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            jobAdapter.submitList(jobs)
            binding.jobsRecyclerView.isVisible = jobs.isNotEmpty()
            binding.emptyText.isVisible = jobs.isEmpty()
        }

        userJobsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        userJobsViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                userJobsViewModel.clearError()
            }
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            showCreateJobDialog()
        }
    }

    private fun showCreateJobDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_job, null)
        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.jobNameInput)
        val positionInput = dialogView.findViewById<TextInputEditText>(R.id.jobPositionInput)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Добавить место работы")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val name = nameInput?.text.toString()
                val position = positionInput?.text.toString()
                if (name.isNotBlank() && position.isNotBlank()) {
                    userJobsViewModel.createJob(
                        userId = userId,
                        name = name,
                        position = position,
                        start = Instant.now()
                    )
                } else {
                    Snackbar.make(binding.root, "Заполните все поля", Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showJobMenuDialog(job: Job) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Действия")
            .setItems(arrayOf("Редактировать", "Удалить")) { _, which ->
                when (which) {
                    0 -> showEditJobDialog(job)
                    1 -> showDeleteConfirmationDialog(job)
                }
            }
            .show()
    }

    private fun showEditJobDialog(job: Job) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_job, null)
        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.jobNameInput)
        val positionInput = dialogView.findViewById<TextInputEditText>(R.id.jobPositionInput)

        nameInput?.setText(job.name)
        positionInput?.setText(job.position)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Редактировать место работы")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val name = nameInput?.text.toString()
                val position = positionInput?.text.toString()
                if (name.isNotBlank() && position.isNotBlank()) {
                    // TODO: Реализовать обновление работы
                    userJobsViewModel.loadUserJobs(userId)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(job: Job) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удаление")
            .setMessage("Вы уверены, что хотите удалить это место работы?")
            .setPositiveButton("Удалить") { _, _ ->
                userJobsViewModel.deleteJob(job.id, userId)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}