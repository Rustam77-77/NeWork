package ru.netology.nework.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import ru.netology.nework.databinding.FragmentEditProfileBinding
import ru.netology.nework.viewmodel.UserViewModel
import ru.netology.nework.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.ivAvatar.load(it) {
                crossfade(true)
                placeholder(ru.netology.nework.R.drawable.ic_avatar_placeholder)
                error(ru.netology.nework.R.drawable.ic_avatar_placeholder)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        loadUserData()
    }

    private fun setupListeners() {
        binding.btnSelectAvatar.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            saveProfile()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadUserData() {
        authViewModel.currentUserId.observe(viewLifecycleOwner) { userId ->
            userId?.let {
                userViewModel.getUserById(it)
            }
        }

        userViewModel.selectedUser.observe(viewLifecycleOwner) { userWithJobs ->
            if (userWithJobs != null) {
                binding.etName.setText(userWithJobs.name)
                binding.etLogin.setText(userWithJobs.login)

                if (!userWithJobs.avatar.isNullOrBlank()) {
                    binding.ivAvatar.load(userWithJobs.avatar) {
                        crossfade(true)
                        placeholder(ru.netology.nework.R.drawable.ic_avatar_placeholder)
                        error(ru.netology.nework.R.drawable.ic_avatar_placeholder)
                    }
                }
            }
        }
    }

    private fun saveProfile() {
        val name = binding.etName.text.toString()
        val login = binding.etLogin.text.toString()

        if (name.isBlank()) {
            binding.tilName.error = "Имя не может быть пустым"
            return
        }

        if (login.isBlank()) {
            binding.tilLogin.error = "Логин не может быть пустым"
            return
        }

        Toast.makeText(requireContext(), "Профиль обновлен", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}