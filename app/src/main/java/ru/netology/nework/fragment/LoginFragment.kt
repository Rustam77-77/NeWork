package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentLoginBinding
import ru.netology.nework.model.AuthState
import ru.netology.nework.viewmodel.AuthViewModel

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val login = binding.etLogin.text.toString()
            val password = binding.etPassword.text.toString()

            if (login.isNotBlank() && password.isNotBlank()) {
                authViewModel.authenticate(login, password)
            }
        }

        binding.tvRegister.setOnClickListener {
            // Временно просто показываем сообщение или ничего не делаем
            binding.tvError.text = "Переход на регистрацию временно недоступен"
            binding.tvError.visibility = View.VISIBLE
        }

        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                AuthState.LOADING -> {
                    binding.btnLogin.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                }
                AuthState.SUCCESS -> {
                    binding.btnLogin.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.text = "Вход выполнен успешно"
                    binding.tvError.visibility = View.VISIBLE
                }
                AuthState.ERROR -> {
                    binding.btnLogin.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    authViewModel.clearError()
                }
                else -> {
                    binding.btnLogin.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        authViewModel.authError.observe(viewLifecycleOwner) { error ->
            error?.let {
                binding.tvError.text = it
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}