package ru.netology.nework.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nework.databinding.FragmentCreatePostBinding
import ru.netology.nework.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.buttonSave.setOnClickListener {
            val content = binding.editTextContent.text.toString()
            if (content.isNotBlank()) {
                // TODO: создать пост
                Toast.makeText(requireContext(), "Пост создан", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Введите текст поста", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonLocation.setOnClickListener {
            Toast.makeText(requireContext(), "Выбор локации", Toast.LENGTH_SHORT).show()
        }

        binding.buttonMentions.setOnClickListener {
            Toast.makeText(requireContext(), "Выбор упоминаний", Toast.LENGTH_SHORT).show()
        }

        binding.buttonImage.setOnClickListener {
            Toast.makeText(requireContext(), "Выбор изображения", Toast.LENGTH_SHORT).show()
        }

        binding.buttonAttachment.setOnClickListener {
            Toast.makeText(requireContext(), "Выбор вложения", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        // TODO: наблюдать за состоянием создания поста
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}