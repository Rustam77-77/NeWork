package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentEditPostBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.viewmodel.PostViewModel

@AndroidEntryPoint
class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            val content = binding.etContent.text.toString()

            if (content.isBlank()) {
                return@setOnClickListener
            }

            // Временно просто возвращаемся назад
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