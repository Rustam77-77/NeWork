package ru.netology.nework.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentUserDetailBinding
import ru.netology.nework.presentation.adapters.UserPagerAdapter
import ru.netology.nework.presentation.viewmodels.UserViewModel

@AndroidEntryPoint
class UserDetailFragment : Fragment() {

    private var _binding: FragmentUserDetailBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()

    private var userId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            userId = it.getLong("userId", 0)
        }

        setupViewPager()
        setupObservers()
        userViewModel.loadUserById(userId)
    }

    private fun setupViewPager() {
        val tabTitles = arrayOf("Стена", "Работы")

        val adapter = UserPagerAdapter(this, userId)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun setupObservers() {
        userViewModel.selectedUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.userName.text = it.name
                binding.userLogin.text = "@${it.login}"
            }
        }

        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        userViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                userViewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}