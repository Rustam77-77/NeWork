package ru.netology.nework.ui.fragment

<<<<<<< HEAD
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.google.android.material.tabs.TabLayoutMediator
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentUserDetailBinding
import ru.netology.nework.dto.User
import ru.netology.nework.adapter.UserPagerAdapter

=======
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
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
class UserDetailFragment : Fragment() {

    private var _binding: FragmentUserDetailBinding? = null
    private val binding get() = _binding!!

<<<<<<< HEAD
=======
    private val userViewModel: UserViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private var currentUser: User? = null
    private var isCurrentUser = false

>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailBinding.inflate(inflater, container, false)
<<<<<<< HEAD
=======
        setHasOptionsMenu(true)
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

<<<<<<< HEAD
        val user = arguments?.getSerializable("user") as? User
        user?.let { displayUser(it) }

        setupViewPager()
    }

    private fun displayUser(user: User) {
        binding.apply {
            if (!user.avatar.isNullOrBlank()) {
                ivAvatar.load(user.avatar) {
                    crossfade(true)
                    placeholder(R.drawable.ic_avatar_placeholder)
                    error(R.drawable.ic_avatar_placeholder)
                }
            }

            tvName.text = user.name
            tvLogin.text = "@${user.login}"
=======
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
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
        }
    }

    private fun setupViewPager() {
<<<<<<< HEAD
        val pagerAdapter = UserPagerAdapter(requireActivity())
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Стена"
                1 -> "Работы"
                else -> ""
            }
        }.attach()
=======
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
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
    }

    override fun onDestroyView() {
        super.onDestroyView()
<<<<<<< HEAD
=======
        // Восстанавливаем заголовок AppBar
        (activity as? AppCompatActivity)?.supportActionBar?.title = "NeWork"
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = null
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
        _binding = null
    }
}