package ru.netology.nework.ui.fragment

import android.graphics.BitmapFactory
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
import androidx.navigation.fragment.findNavController
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentRegisterBinding
import ru.netology.nework.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    private var selectedImageUri: Uri? = null
    private var avatarError: String? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // Проверяем размер изображения
            if (validateImageSize(it)) {
                selectedImageUri = it
                avatarError = null
                binding.tilAvatar.error = null
                binding.ivAvatar.load(it) {
                    crossfade(true)
                    placeholder(R.drawable.ic_avatar_placeholder)
                    error(R.drawable.ic_avatar_placeholder)
                }
            } else {
                avatarError = "Изображение должно быть не больше 2048x2048"
                binding.tilAvatar.error = avatarError
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
        binding.btnSelectAvatar.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.btnRegister.setOnClickListener {
            val login = binding.etLogin.text.toString()
            val password = binding.etPassword.text.toString()
            val name = binding.etName.text.toString()

            if (validateInputs()) {
                if (selectedImageUri != null) {
                    registerWithAvatar(login, password, name)
                } else {
                    viewModel.register(login, password, name)
                }
            }
        }

        binding.btnToLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validateImageSize(uri: Uri): Boolean {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            val width = options.outWidth
            val height = options.outHeight

            width <= 2048 && height <= 2048
        } catch (e: Exception) {
            false
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
                    binding.btnRegister.isEnabled = false
                }
                is AuthViewModel.AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
                }
                is AuthViewModel.AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
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
        binding.etLogin.addTextChangedListener { validateInputs() }
        binding.etPassword.addTextChangedListener { validateInputs() }
        binding.etConfirmPassword.addTextChangedListener { validateInputs() }
        binding.etName.addTextChangedListener { validateInputs() }
    }

    private fun validateInputs(): Boolean {
        val login = binding.etLogin.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        val name = binding.etName.text.toString()

        var isValid = true

        // Проверка логина - непустая строка
        if (login.isBlank()) {
            binding.tilLogin.error = "Логин не может быть пустым"
            isValid = false
        } else {
            binding.tilLogin.error = null
        }

        // Проверка имени - непустая строка
        if (name.isBlank()) {
            binding.tilName.error = "Имя не может быть пустым"
            isValid = false
        } else {
            binding.tilName.error = null
        }

        // Проверка пароля - непустая строка
        if (password.isBlank()) {
            binding.tilPassword.error = "Пароль не может быть пустым"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Пароль должен содержать минимум 6 символов"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        // Проверка подтверждения пароля
        if (confirmPassword.isBlank()) {
            binding.tilConfirmPassword.error = "Подтверждение пароля не может быть пустым"
            isValid = false
        } else if (confirmPassword != password) {
            binding.tilConfirmPassword.error = "Пароли не совпадают"
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }

        // Проверка аватара (если выбран)
        if (selectedImageUri != null) {
            if (avatarError != null) {
                binding.tilAvatar.error = avatarError
                isValid = false
            } else {
                binding.tilAvatar.error = null
            }
        }

        // Кнопка регистрации активна только если все поля валидны
        binding.btnRegister.isEnabled = isValid

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}