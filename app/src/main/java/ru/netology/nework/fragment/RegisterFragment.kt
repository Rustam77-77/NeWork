package ru.netology.nework.fragment
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.viewmodel.AuthViewModel
@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private val viewModel: AuthViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Находим вью через findViewById напрямую из ресурсов
        val nameField = view.findViewById<EditText>(R.id.nameField)
        val loginField = view.findViewById<EditText>(R.id.loginField)
        val passwordField = view.findViewById<EditText>(R.id.passwordField)
        val registerButton = view.findViewById<MaterialButton>(R.id.registerButton)
        registerButton?.setOnClickListener {
            val name = nameField?.text?.toString() ?: ""
            val login = loginField?.text?.toString() ?: ""
            val pass = passwordField?.text?.toString() ?: ""
            if (name.isBlank() || login.isBlank() || pass.isBlank()) {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
            } else {
                // Передаем все три параметра в ViewModel
                viewModel.register(login, pass, name)
            }
        }
        // Следим за состоянием авторизации
        viewModel.authorized.observe(viewLifecycleOwner) { auth ->
            if (auth?.token != null) {
                findNavController().navigateUp()
            }
        }
    }
}