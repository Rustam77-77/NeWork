package ru.netology.nework.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ru.netology.nework.databinding.FragmentRegisterBinding
import ru.netology.nework.viewmodel.AuthViewModel
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.R
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.imageViewAvatar.load(it) {
                crossfade(true)
                placeholder(R.drawable.ic_avatar_placeholder)
                error(R.drawable.ic_avatar_placeholder)
            }
            validateInputs()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
        setupValidation()
    }

    private fun setupListeners() {
        binding.buttonSelectAvatar.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.buttonRegister.setOnClickListener {
            val login = binding.editTextLogin.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()
            val name = binding.editTextName.text.toString()

            if (validateInputs()) {
                if (selectedImageUri != null) {
                    registerWithAvatar(login, password, name)
                } else {
                    viewModel.register(login, password, name)
                }
            }
        }

        binding.buttonToLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun registerWithAvatar(login: String, password: String, name: String) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(selectedImageUri!!)
            val file = File(requireContext().cacheDir, "avatar_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            val requestFile = file.asRequestBody("image/jpeg".toMediaType())
            val avatarPart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            viewModel.registerWithAvatar(login, password, name, avatarPart)

            // Очищаем временный файл
            file.delete()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Ошибка при загрузке аватара", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonRegister.isEnabled = false
                }
                is AuthViewModel.AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(
                        R.id.action_registerFragment_to_mainFragment
                    )
                }
                is AuthViewModel.AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonRegister.isEnabled = true
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonRegister.isEnabled = true
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun setupValidation() {
        binding.editTextLogin.addTextChangedListener { validateInputs() }
        binding.editTextPassword.addTextChangedListener { validateInputs() }
        binding.editTextConfirmPassword.addTextChangedListener { validateInputs() }
        binding.editTextName.addTextChangedListener { validateInputs() }
    }

    private fun validateInputs(): Boolean {
        val login = binding.editTextLogin.text.toString()
        val password = binding.editTextPassword.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()
        val name = binding.editTextName.text.toString()

        var isValid = true

        // Проверка логина
        if (login.isBlank()) {
            binding.textInputLayoutLogin.error = "Логин не может быть пустым"
            isValid = false
        } else {
            binding.textInputLayoutLogin.error = null
        }

        // Проверка имени
        if (name.isBlank()) {
            binding.textInputLayoutName.error = "Имя не может быть пустым"
            isValid = false
        } else {
            binding.textInputLayoutName.error = null
        }

        // Проверка пароля
        if (password.isBlank()) {
            binding.textInputLayoutPassword.error = "Пароль не может быть пустым"
            isValid = false
        } else if (password.length < 6) {
            binding.textInputLayoutPassword.error = "Пароль должен содержать минимум 6 символов"
            isValid = false
        } else {
            binding.textInputLayoutPassword.error = null
        }

        // Проверка подтверждения пароля
        if (confirmPassword != password) {
            binding.textInputLayoutConfirmPassword.error = "Пароли не совпадают"
            isValid = false
        } else {
            binding.textInputLayoutConfirmPassword.error = null
        }

        binding.buttonRegister.isEnabled = isValid
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}