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
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentCreateJobBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.viewmodel.UserJobsViewModel
import ru.netology.nework.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CreateJobFragment : Fragment() {

    private var _binding: FragmentCreateJobBinding? = null
    private val binding get() = _binding!!

    private val jobsViewModel: UserJobsViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private var userId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateJobBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = arguments?.getLong("userId") ?: 0

        setupDatePickers()
        setupListeners()
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()

        binding.etStart.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                binding.etStart.setText(date)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.etFinish.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                binding.etFinish.setText(date)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveJob()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun saveJob() {
        val name = binding.etName.text.toString()
        val position = binding.etPosition.text.toString()
        val start = binding.etStart.text.toString()
        val finish = binding.etFinish.text.toString().takeIf { it.isNotBlank() }
        val link = binding.etLink.text.toString().takeIf { it.isNotBlank() }

        if (name.isBlank()) {
            binding.tilName.error = "Введите название компании"
            return
        }

        if (position.isBlank()) {
            binding.tilPosition.error = "Введите должность"
            return
        }

        if (start.isBlank()) {
            binding.tilStart.error = "Выберите дату начала"
            return
        }

        if (userId == 0L) {
            authViewModel.currentUserId.observe(viewLifecycleOwner) { id ->
                if (id != null) {
                    createJob(id, name, position, start, finish, link)
                }
            }
        } else {
            createJob(userId, name, position, start, finish, link)
        }
    }

    private fun createJob(userId: Long, name: String, position: String, start: String, finish: String?, link: String?) {
        val job = Job(
            id = 0,
            name = name,
            position = position,
            start = start,
            finish = finish,
            link = link
        )

        jobsViewModel.saveJob(userId, job)
        Toast.makeText(requireContext(), "Работа добавлена", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}