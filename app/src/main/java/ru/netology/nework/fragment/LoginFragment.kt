package ru.netology.nework.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentLoginBinding
import ru.netology.nework.viewmodel.AuthViewModel
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        // Исправьте loginButton, loginField, passwordField на ваши реальные ID из XML
        binding.loginButton.setOnClickListener {
            val login = binding.loginField.text?.toString() ?: ""
            val pass = binding.passwordField.text?.toString() ?: ""
            if (login.isNotBlank() && pass.isNotBlank()) {
                viewModel.login(login, pass)
            }
        }
        viewModel.authorized.observe(viewLifecycleOwner) {
            if (it?.token != null) findNavController().navigateUp()
        }
        return binding.root
    }
}