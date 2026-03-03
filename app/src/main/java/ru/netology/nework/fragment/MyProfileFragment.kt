package ru.netology.nework.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.google.android.material.tabs.TabLayoutMediator
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentMyProfileBinding
import ru.netology.nework.adapter.UserPagerAdapter
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        setupObservers()
        setupListeners()

        loadUserProfile()
    }

    private fun setupViewPager() {
        val pagerAdapter = UserPagerAdapter(requireActivity())
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Стена"
                1 -> "Работы"
                else -> ""
            }
        }.attach()
    }

    private fun setupObservers() {
        authViewModel.currentUserId.observe(viewLifecycleOwner) { userId ->
            userId?.let {
                userViewModel.getUserById(it)
            }
        }

        userViewModel.selectedUser.observe(viewLifecycleOwner) { userWithJobs ->
            if (userWithJobs != null) {
                displayUserProfile(userWithJobs)
            }
        }

        userViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                userViewModel.clearError()
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_editProfileFragment)
        }
    }

    private fun loadUserProfile() {
        authViewModel.currentUserId.observe(viewLifecycleOwner) { userId ->
            userId?.let {
                userViewModel.getUserById(it)
            }
        }
    }

    private fun displayUserProfile(userWithJobs: ru.netology.nework.dto.UserWithJobs) {
        binding.apply {
            if (!userWithJobs.avatar.isNullOrBlank()) {
                ivAvatar.load(userWithJobs.avatar) {
                    crossfade(true)
                    placeholder(R.drawable.ic_avatar_placeholder)
                    error(R.drawable.ic_avatar_placeholder)
                }
            }

            tvName.text = userWithJobs.name
            tvLogin.text = "@${userWithJobs.login}"
        }
    }

    private fun showLogoutDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Выход")
            .setMessage("Вы уверены, что хотите выйти?")
            .setPositiveButton("Выйти") { _, _ ->
                authViewModel.logout()
                findNavController().navigate(R.id.action_myProfileFragment_to_loginFragment)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}