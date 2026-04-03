package ru.netology.nework.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.netology.nework.presentation.fragments.UserJobsFragment
import ru.netology.nework.presentation.fragments.UserWallFragment

class UserPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val userId: Long
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserWallFragment.newInstance(userId)
            1 -> UserJobsFragment.newInstance(userId)
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}