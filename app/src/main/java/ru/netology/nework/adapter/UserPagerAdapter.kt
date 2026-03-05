package ru.netology.nework.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.netology.nework.ui.fragment.UserWallFragment
import ru.netology.nework.ui.fragment.UserJobsFragment

class UserPagerAdapter(fragment: Fragment, private val userId: Long) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserWallFragment.newInstance(userId)
            1 -> UserJobsFragment.newInstance(userId)
            else -> throw IndexOutOfBoundsException()
        }
    }
}