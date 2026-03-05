package ru.netology.nework.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.adapter.UserPagerAdapter
import ru.netology.nework.databinding.FragmentUserDetailBinding
import ru.netology.nework.dto.User
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.UserViewModel

@AndroidEntryPoint
class UserDetailFragment : Fragment() {

    private var _binding: FragmentUserDetailBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private var currentUser: User? = null
    private var isCurrentUser = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем пользователя из аргументов
        currentUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("user", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("user") as? User
        }

        // Проверяем, является ли текущий пользователь владельцем профиля
        authViewModel.currentUserId.observe(viewLifecycleOwner) { userId ->
            isCurrentUser = userId == currentUser?.id
            activity?.invalidateOptionsMenu()
        }

        setupViewPager()
        displayUserInfo()
        setupAppBar()
    }

    private fun setupAppBar() {
        // Устанавливаем название в AppBar
        currentUser?.let { user ->
            (activity as? AppCompatActivity)?.supportActionBar?.title = user.name
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "@${user.login}"
        }
    }

    private fun setupViewPager() {
        currentUser?.let { user ->
            val pagerAdapter = UserPagerAdapter(this, user.id)
            binding.viewPager.adapter = pagerAdapter

            // Привязываем TabLayout к ViewPager
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Стена"
                    1 -> "Работы"
                    else -> ""
                }
            }.attach()
        }
    }

    private fun displayUserInfo() {
        currentUser?.let { user ->
            // Загрузка аватара
            if (!user.avatar.isNullOrBlank()) {
                binding.ivAvatar.load(user.avatar) {
                    crossfade(true)
                    placeholder(R.drawable.ic_avatar_placeholder)
                    error(R.drawable.ic_avatar_placeholder)
                }
            } else {
                binding.ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
            }

            // Имя и логин (дублируем для информации под аватаром)
            binding.tvName.text = user.name
            binding.tvLogin.text = "@${user.login}"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.user_detail_menu, menu)

        // Показываем кнопку редактирования только для своего профиля
        menu.findItem(R.id.action_edit)?.isVisible = isCurrentUser

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                // Переход к редактированию профиля
                if (isCurrentUser) {
                    findNavController().navigate(R.id.action_userDetailFragment_to_editProfileFragment)
                }
                true
            }
            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Восстанавливаем заголовок AppBar
        (activity as? AppCompatActivity)?.supportActionBar?.title = "NeWork"
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = null
        _binding = null
    }
}