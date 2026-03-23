package ru.netology.nework.presentation.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentProfileBinding
import ru.netology.nework.presentation.adapters.UserPagerAdapter
import ru.netology.nework.presentation.viewmodels.AuthViewModel
import ru.netology.nework.presentation.viewmodels.UserJobsViewModel
import ru.netology.nework.presentation.viewmodels.UserViewModel
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val jobsViewModel: UserJobsViewModel by viewModels()

    private var userId: Long = 0
    private var isOwnProfile: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            userId = it.getLong("userId", 0)
        }

        val currentUserId = authViewModel.currentUserId.value ?: 0
        isOwnProfile = userId == currentUserId

        setupViewPager()
        setupObservers()
        setupListeners()

        userViewModel.loadUserById(userId)
        jobsViewModel.loadJobsForUser(userId)
    }

    private fun setupViewPager() {
        val tabTitles = arrayOf("Стена", "Работы")

        val adapter = UserPagerAdapter(this, userId)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun setupObservers() {
        userViewModel.selectedUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.userName.text = it.name
                binding.userLogin.text = "@${it.login}"
            }
        }

        authViewModel.currentUserId.observe(viewLifecycleOwner) { currentId ->
            isOwnProfile = userId == currentId
            if (isOwnProfile) {
                binding.fabAddJob.visibility = View.VISIBLE
            } else {
                binding.fabAddJob.visibility = View.GONE
            }
        }

        jobsViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                jobsViewModel.clearError()
            }
        }

        jobsViewModel.isCreated.observe(viewLifecycleOwner) { isCreated ->
            if (isCreated) {
                jobsViewModel.refreshJobs()
                jobsViewModel.clearCreated()
            }
        }

        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        userViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                userViewModel.clearError()
            }
        }
    }

    private fun setupListeners() {
        binding.fabAddJob.setOnClickListener {
            if (isOwnProfile) {
                showAddJobDialog()
            }
        }
    }

    private fun showAddJobDialog() {
        // Создаем диалог программно
        val dialogView = layoutInflater.inflate(ru.netology.nework.R.layout.dialog_add_job, null)
        val companyInput = dialogView.findViewById<EditText>(ru.netology.nework.R.id.companyInput)
        val positionInput = dialogView.findViewById<EditText>(ru.netology.nework.R.id.positionInput)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Добавить место работы")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val company = companyInput?.text.toString()
                val position = positionInput?.text.toString()

                if (company.isNotBlank() && position.isNotBlank()) {
                    showStartDatePickerDialog(company, position)
                } else {
                    Snackbar.make(binding.root, "Заполните все поля", Snackbar.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showStartDatePickerDialog(company: String, position: String) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth, 0, 0, 0)
            val startDate = calendar.time
            showEndDatePickerDialog(company, position, startDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showEndDatePickerDialog(company: String, position: String, startDate: Date) {
        val calendar = Calendar.getInstance()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Дата окончания")
            .setMessage("Укажите дату окончания работы или выберите 'По настоящее время'")
            .setPositiveButton("Выбрать дату") { _, _ ->
                DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth, 0, 0, 0)
                    val endDate = calendar.time
                    jobsViewModel.createJob(company, position, startDate, endDate)
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
            }
            .setNeutralButton("По настоящее время") { _, _ ->
                jobsViewModel.createJob(company, position, startDate, null)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}