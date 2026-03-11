package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.ItemUserBinding
import ru.netology.nework.dto.User

class UserAdapter(
<<<<<<< HEAD
    private val onItemClick: (User) -> Unit
=======
    private val onItemClickListener: (User) -> Unit
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.apply {
<<<<<<< HEAD
                if (!user.avatar.isNullOrBlank()) {
                    ivAvatar.load(user.avatar) {
=======
                // Загрузка аватара
                if (!user.avatar.isNullOrBlank()) {
                    avatarImageView.load(user.avatar) {
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                        crossfade(true)
                        placeholder(R.drawable.ic_avatar_placeholder)
                        error(R.drawable.ic_avatar_placeholder)
                    }
                } else {
<<<<<<< HEAD
                    ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
                }

                tvName.text = user.name
                tvLogin.text = "@${user.login}"

                root.setOnClickListener { onItemClick(user) }
=======
                    avatarImageView.setImageResource(R.drawable.ic_avatar_placeholder)
                }

                // Имя и логин
                nameTextView.text = user.name
                loginTextView.text = "@${user.login}"

                root.setOnClickListener {
                    onItemClickListener(user)
                }
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}