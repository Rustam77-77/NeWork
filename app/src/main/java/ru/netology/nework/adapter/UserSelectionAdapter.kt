package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import ru.netology.nework.R
import ru.netology.nework.databinding.ItemUserSelectionBinding
import ru.netology.nework.dto.User

class UserSelectionAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<User, UserSelectionAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserViewHolder(
        private val binding: ItemUserSelectionBinding,
        private val onInteractionListener: OnInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.apply {
                if (!user.avatar.isNullOrBlank()) {
                    avatar.load(user.avatar) {
                        placeholder(R.drawable.ic_default_avatar)
                        error(R.drawable.ic_default_avatar)
                        transformations(CircleCropTransformation())
                    }
                } else {
                    avatar.setImageResource(R.drawable.ic_default_avatar)
                }

                name.text = user.name
                login.text = "@${user.login}"

                checkbox.isChecked = false

                root.setOnClickListener {
                    checkbox.isChecked = !checkbox.isChecked
                    onInteractionListener.onUserSelected(user, checkbox.isChecked)
                }
            }
        }
    }

    interface OnInteractionListener {
        fun onUserSelected(user: User, isSelected: Boolean)
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem == newItem
    }
}