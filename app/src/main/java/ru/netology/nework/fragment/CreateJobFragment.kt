package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentCreateJobBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.viewmodel.UserJobsViewModel

@AndroidEntryPoint
class CreateJobFragment : Fragment() {

    private var _binding: FragmentCreateJobBinding? = null
    private val binding get() = _binding!!

    private val userJobsViewModel: UserJobsViewModel by viewModels()

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

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val position = binding.etPosition.text.toString()
            val start = binding.etStart.text.toString()
            val finish = binding.etFinish.text.toString()
            val link = binding.etLink.text.toString()

            if (name.isBlank() || position.isBlank() || start.isBlank()) {
                return@setOnClickListener
            }

            val job = Job(
                id = 0,
                name = name,
                position = position,
                start = start,
                finish = finish.ifBlank { null },
                link = link.ifBlank { null },
                ownedByMe = true
            )

            userJobsViewModel.saveJob(job)
            findNavController().navigateUp()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}