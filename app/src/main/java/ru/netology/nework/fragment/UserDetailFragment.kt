package ru.netology.nework.ui.fragment

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

class UserDetailFragment : Fragment() {

    private var _binding: FragmentUserDetailBinding? = null
    private val binding get() = _binding!!

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
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}