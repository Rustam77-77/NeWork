package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.ItemUserSelectionBinding
import ru.netology.nework.dto.User

class UserSelectionAdapter(
    private val onUserSelected: (User, Boolean) -> Unit
) : ListAdapter<User, UserSelectionAdapter.UserViewHolder>(UserDiffCallback()) {

    private val selectedUsers = mutableSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserSelectionBinding.inflate(
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
        private val binding: ItemUserSelectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.apply {
                // Загрузка аватара
                if (!user.avatar.isNullOrBlank()) {
                    ivAvatar.load(user.avatar) {
                        crossfade(true)
                        placeholder(R.drawable.ic_avatar_placeholder)
                        error(R.drawable.ic_avatar_placeholder)
                    }
                } else {
                    ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
                }

                // Имя и логин
                tvName.text = user.name
                tvLogin.text = "@${user.login}"

                // Состояние выбора
                cbSelected.isChecked = selectedUsers.contains(user.id)

                // Обработчик выбора
                root.setOnClickListener {
                    cbSelected.isChecked = !cbSelected.isChecked
                }

                cbSelected.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedUsers.add(user.id)
                    } else {
                        selectedUsers.remove(user.id)
                    }
                    onUserSelected(user, isChecked)
                }
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