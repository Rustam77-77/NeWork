package ru.netology.nework.adapter

import androidx.fragment.app.Fragment
<<<<<<< HEAD
import androidx.fragment.app.FragmentActivity
=======
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.netology.nework.ui.fragment.UserWallFragment
import ru.netology.nework.ui.fragment.UserJobsFragment

<<<<<<< HEAD
class UserPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
=======
class UserPagerAdapter(fragment: Fragment, private val userId: Long) : FragmentStateAdapter(fragment) {
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
<<<<<<< HEAD
            0 -> UserWallFragment()
            1 -> UserJobsFragment()
=======
            0 -> UserWallFragment.newInstance(userId)
            1 -> UserJobsFragment.newInstance(userId)
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            else -> throw IndexOutOfBoundsException()
        }
    }
}