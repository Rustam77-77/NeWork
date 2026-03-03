package ru.netology.nework.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.netology.nework.ui.fragment.UserWallFragment
import ru.netology.nework.ui.fragment.UserJobsFragment

class UserPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserWallFragment()
            1 -> UserJobsFragment()
            else -> throw IndexOutOfBoundsException()
        }
    }
}