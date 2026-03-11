package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import ru.netology.nework.R
import ru.netology.nework.databinding.CardPostBinding
import ru.netology.nework.dto.Post

class PostAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PostViewHolder(
        private val binding: CardPostBinding,
        private val onInteractionListener: OnInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                authorName.text = post.author
                authorJob.text = post.authorJob ?: ""
                published.text = post.published
                content.text = post.content
                likesCounter.text = post.likeOwnerIds?.size?.toString() ?: "0"

                // Устанавливаем состояние кнопки лайка
                like.setImageResource(
                    if (post.likedByMe == true) R.drawable.ic_like_checked
                    else R.drawable.ic_like
                )

                // Загрузка аватара автора
                if (!post.authorAvatar.isNullOrBlank()) {
                    authorAvatar.load(post.authorAvatar) {
                        placeholder(R.drawable.ic_default_avatar)
                        error(R.drawable.ic_default_avatar)
                        transformations(CircleCropTransformation())
                    }
                } else {
                    authorAvatar.setImageResource(R.drawable.ic_default_avatar)
                }

                // Загрузка вложения
                if (post.attachment?.url != null) {
                    attachmentImage.visibility = ViewGroup.VISIBLE
                    attachmentImage.load(post.attachment.url) {
                        placeholder(R.drawable.ic_image_placeholder)
                        error(R.drawable.ic_image_placeholder)
                    }
                } else {
                    attachmentImage.visibility = ViewGroup.GONE
                }

                like.setOnClickListener {
                    onInteractionListener.onLikeClicked(post)
                }

                root.setOnClickListener {
                    onInteractionListener.onPostClicked(post)
                }

                root.setOnLongClickListener {
                    if (post.ownedByMe == true) {
                        onInteractionListener.onRemoveClicked(post)
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }

    interface OnInteractionListener {
        fun onLikeClicked(post: Post)
        fun onPostClicked(post: Post)
        fun onRemoveClicked(post: Post)
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem == newItem
    }
}