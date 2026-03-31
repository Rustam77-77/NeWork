package ru.netology.nework.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.ItemUserBinding
import ru.netology.nework.dto.User

class UserAdapter(
    private val onItemClickListener: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    companion object {
        private const val TAG = "UserAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    class UserViewHolder(
        private val binding: ItemUserBinding,
        private val onItemClickListener: (User) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.apply {
                userName.text = user.name
                userLogin.text = "@${user.login}"

                val initials = user.name.split(" ")
                    .take(2)
                    .map { it.firstOrNull() ?: '?' }
                    .joinToString("")
                avatarText.text = initials

                root.setOnClickListener {
                    onItemClickListener(user)
                }
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
    }
}