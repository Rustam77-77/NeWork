package ru.netology.nework.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentUserJobsBinding
import ru.netology.nework.adapter.JobAdapter
import ru.netology.nework.dto.Job
import ru.netology.nework.viewmodel.UserJobsViewModel
import ru.netology.nework.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class UserJobsFragment : Fragment() {

    private var _binding: FragmentUserJobsBinding? = null
    private val binding get() = _binding!!

    private val jobsViewModel: UserJobsViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var jobAdapter: JobAdapter
    private var isCurrentUser = false
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

        setupRecyclerView()
        setupListeners()
        setupObservers()

        userId = (parentFragment as? UserDetailFragment)?.arguments?.getSerializable("user")?.let {
            (it as ru.netology.nework.dto.User).id
        } ?: 0

        checkIfCurrentUser()
        jobsViewModel.loadUserJobs(userId)
    }

    private fun checkIfCurrentUser() {
        authViewModel.currentUserId.observe(viewLifecycleOwner) { currentUserId ->
            isCurrentUser = currentUserId == userId
            binding.fabAddJob.visibility = if (isCurrentUser) View.VISIBLE else View.GONE
        }
    }

    private fun setupRecyclerView() {
        jobAdapter = JobAdapter(
            onDeleteClick = { job ->
                if (isCurrentUser) {
                    showDeleteJobDialog(job)
                }
            },
            onEditClick = { job ->
                if (isCurrentUser) {
                    showEditJobDialog(job)
                }
            }
        )

        binding.rvJobs.apply {
            adapter = jobAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupListeners() {
        binding.fabAddJob.setOnClickListener {
            findNavController().navigate(R.id.action_userJobsFragment_to_createJobFragment)
        }
    }

    private fun setupObservers() {
        jobsViewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            jobAdapter.submitList(jobs)
            binding.tvEmpty.visibility = if (jobs.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        jobsViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                jobsViewModel.clearError()
            }
        }
    }

    private fun showEditJobDialog(job: Job) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_job, null)
        val nameInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etName)
        val positionInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPosition)
        val startInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etStart)
        val finishInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFinish)
        val linkInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etLink)

        nameInput.setText(job.name)
        positionInput.setText(job.position)
        startInput.setText(job.start)
        finishInput.setText(job.finish)
        linkInput.setText(job.link)

        val calendar = Calendar.getInstance()

        startInput.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                startInput.setText(date)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        finishInput.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                finishInput.setText(date)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Редактировать работу")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val name = nameInput.text.toString()
                val position = positionInput.text.toString()
                val start = startInput.text.toString()
                val finish = finishInput.text.toString().takeIf { it.isNotBlank() }
                val link = linkInput.text.toString().takeIf { it.isNotBlank() }

                if (name.isNotBlank() && position.isNotBlank() && start.isNotBlank()) {
                    val updatedJob = job.copy(
                        name = name,
                        position = position,
                        start = start,
                        finish = finish,
                        link = link
                    )
                    jobsViewModel.saveJob(userId, updatedJob)
                } else {
                    Toast.makeText(requireContext(), "Заполните обязательные поля", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showDeleteJobDialog(job: Job) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удаление работы")
            .setMessage("Вы уверены, что хотите удалить работу в ${job.name}?")
            .setPositiveButton("Удалить") { _, _ ->
                jobsViewModel.deleteJob(userId, job.id)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}