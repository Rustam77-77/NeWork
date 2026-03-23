package ru.netology.nework.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.dto.User

class UserSelectionAdapter(
    private val users: List<User>,
    private val selectedUserIds: List<Long>,
    private val onUserSelected: (Long, Boolean) -> Unit
) : RecyclerView.Adapter<UserSelectionAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_selection, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.userName)
        private val userLogin: TextView = itemView.findViewById(R.id.userLogin)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bind(user: User) {
            userName.text = user.name
            userLogin.text = "@${user.login}"
            checkBox.isChecked = selectedUserIds.contains(user.id)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onUserSelected(user.id, isChecked)
            }

            itemView.setOnClickListener {
                checkBox.isChecked = !checkBox.isChecked
            }
        }
    }
}