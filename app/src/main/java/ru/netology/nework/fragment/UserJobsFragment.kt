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
<<<<<<< HEAD
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentUserJobsBinding
import ru.netology.nework.adapter.JobAdapter
import ru.netology.nework.dto.Job
import ru.netology.nework.viewmodel.UserJobsViewModel
import ru.netology.nework.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
=======
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.adapter.JobAdapter
import ru.netology.nework.databinding.FragmentUserJobsBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.UserJobsViewModel
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
import java.util.*

@AndroidEntryPoint
class UserJobsFragment : Fragment() {

    private var _binding: FragmentUserJobsBinding? = null
    private val binding get() = _binding!!

    private val jobsViewModel: UserJobsViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var jobAdapter: JobAdapter
<<<<<<< HEAD
    private var isCurrentUser = false
    private var userId: Long = 0
=======
    private var userId: Long = 0
    private var isCurrentUser = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getLong("userId") ?: 0
    }
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399

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

<<<<<<< HEAD
        userId = (parentFragment as? UserDetailFragment)?.arguments?.getSerializable("user")?.let {
            (it as ru.netology.nework.dto.User).id
        } ?: 0

=======
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
        checkIfCurrentUser()
        jobsViewModel.loadUserJobs(userId)
    }

    private fun checkIfCurrentUser() {
        authViewModel.currentUserId.observe(viewLifecycleOwner) { currentUserId ->
            isCurrentUser = currentUserId == userId
<<<<<<< HEAD
            binding.fabAddJob.visibility = if (isCurrentUser) View.VISIBLE else View.GONE
=======
            if (isCurrentUser) {
                binding.fabAddJob.visibility = View.VISIBLE
            } else {
                binding.fabAddJob.visibility = View.GONE
            }
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
        }
    }

    private fun setupRecyclerView() {
        jobAdapter = JobAdapter(
<<<<<<< HEAD
            onDeleteClick = { job ->
=======
            onDeleteClickListener = { job ->
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                if (isCurrentUser) {
                    showDeleteJobDialog(job)
                }
            },
<<<<<<< HEAD
            onEditClick = { job ->
=======
            onEditClickListener = { job ->
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                if (isCurrentUser) {
                    showEditJobDialog(job)
                }
            }
        )

        binding.rvJobs.apply {
            adapter = jobAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
<<<<<<< HEAD
=======

        binding.swipeRefreshLayout.setOnRefreshListener {
            jobsViewModel.loadUserJobs(userId)
        }
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
    }

    private fun setupListeners() {
        binding.fabAddJob.setOnClickListener {
<<<<<<< HEAD
            findNavController().navigate(R.id.action_userJobsFragment_to_createJobFragment)
=======
            if (isCurrentUser) {
                val bundle = Bundle().apply {
                    putLong("userId", userId)
                }
                findNavController().navigate(R.id.action_userJobsFragment_to_createJobFragment, bundle)
            }
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
        }
    }

    private fun setupObservers() {
        jobsViewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            jobAdapter.submitList(jobs)
<<<<<<< HEAD
            binding.tvEmpty.visibility = if (jobs.isNullOrEmpty()) View.VISIBLE else View.GONE
=======
            binding.swipeRefreshLayout.isRefreshing = false

            if (jobs.isNullOrEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.tvEmpty.visibility = View.GONE
            }
        }

        jobsViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading == true && jobsViewModel.jobs.value.isNullOrEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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

<<<<<<< HEAD
=======
    companion object {
        fun newInstance(userId: Long): UserJobsFragment {
            val fragment = UserJobsFragment()
            val args = Bundle()
            args.putLong("userId", userId)
            fragment.arguments = args
            return fragment
        }
    }

>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
