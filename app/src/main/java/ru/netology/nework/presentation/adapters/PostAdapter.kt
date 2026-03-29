package ru.netology.nework.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.ItemPostBinding
import ru.netology.nework.dto.Post
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(
    private val onItemClickListener: (Post) -> Unit,
    private val onLikeClickListener: (Post) -> Unit,
    private val onMenuClickListener: (Post, Boolean) -> Unit,
    private val currentUserId: Long? = null
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding, onItemClickListener, onLikeClickListener, onMenuClickListener, currentUserId)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PostViewHolder(
        private val binding: ItemPostBinding,
        private val onItemClickListener: (Post) -> Unit,
        private val onLikeClickListener: (Post) -> Unit,
        private val onMenuClickListener: (Post, Boolean) -> Unit,
        private val currentUserId: Long?
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        fun bind(post: Post) {
            binding.apply {
                authorName.text = post.author
                date.text = dateFormat.format(post.published)
                content.text = post.content
                likeCount.text = post.likeOwnerIds.size.toString()
                likeButton.isChecked = post.likedByMe

                val initials = post.author.split(" ")
                    .take(2)
                    .map { it.firstOrNull() ?: '?' }
                    .joinToString("")
                avatarText.text = initials

                if (post.attachment?.url.isNullOrEmpty()) {
                    linkContainer.visibility = ViewGroup.GONE
                } else {
                    linkContainer.visibility = ViewGroup.VISIBLE
                    linkText.text = post.attachment?.url
                }

                val isAuthor = post.ownedByMe
                menuButton.visibility = if (isAuthor) ViewGroup.VISIBLE else ViewGroup.GONE

                root.setOnClickListener { onItemClickListener(post) }
                likeButton.setOnClickListener { onLikeClickListener(post) }
                menuButton.setOnClickListener { onMenuClickListener(post, isAuthor) }

                linkContainer.setOnClickListener {
                    post.attachment?.url?.let { url ->
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                        root.context.startActivity(intent)
                    }
                }
            }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
    }
}