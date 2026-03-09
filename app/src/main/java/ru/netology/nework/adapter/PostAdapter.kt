package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.ItemPostBinding
import ru.netology.nework.dto.Post
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(
    private val onLikeClickListener: (Post) -> Unit,
    private val onMenuClickListener: (Post) -> Unit,
    private val onItemClickListener: (Post) -> Unit
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PostViewHolder(
        private val binding: ItemPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                // Загрузка аватара
                if (!post.authorAvatar.isNullOrBlank()) {
                    avatarImageView.load(post.authorAvatar) {
                        crossfade(true)
                        placeholder(R.drawable.ic_avatar_placeholder)
                        error(R.drawable.ic_avatar_placeholder)
                    }
                } else {
                    avatarImageView.setImageResource(R.drawable.ic_avatar_placeholder)
                }

                // Имя автора
                authorNameTextView.text = post.author

                // Дата публикации
                try {
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    publishedTextView.text = dateFormat.format(
                        java.time.Instant.parse(post.published).toEpochMilli()
                    )
                } catch (e: Exception) {
                    publishedTextView.text = post.published
                }

                // Текст поста
                contentTextView.text = post.content

                // Лайки
                likesCountTextView.text = post.likes.toString()
                likeImageView.setImageResource(
                    if (post.likedByMe) R.drawable.ic_like_filled
                    else R.drawable.ic_like_outline
                )

                // Обработчики кликов
                likeImageView.setOnClickListener {
                    onLikeClickListener(post)
                }

                menuImageView.setOnClickListener {
                    onMenuClickListener(post)
                }

                root.setOnClickListener {
                    onItemClickListener(post)
                }

                // Отображение вложения
                if (post.attachment != null) {
                    attachmentImageView.visibility = ViewGroup.VISIBLE
                    when (post.attachment.type) {
                        ru.netology.nework.dto.AttachmentType.IMAGE -> {
                            attachmentImageView.load(post.attachment.url) {
                                crossfade(true)
                                placeholder(R.drawable.ic_image_placeholder)
                            }
                        }
                        ru.netology.nework.dto.AttachmentType.VIDEO -> {
                            attachmentImageView.setImageResource(R.drawable.ic_video)
                        }
                        ru.netology.nework.dto.AttachmentType.AUDIO -> {
                            attachmentImageView.setImageResource(R.drawable.ic_audio)
                        }
                    }
                } else {
                    attachmentImageView.visibility = ViewGroup.GONE
                }

                // Ссылка
                if (!post.link.isNullOrBlank()) {
                    linkTextView.visibility = ViewGroup.VISIBLE
                    linkTextView.text = post.link
                    linkTextView.setOnClickListener {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(post.link)
                        )
                        root.context.startActivity(intent)
                    }
                } else {
                    linkTextView.visibility = ViewGroup.GONE
                }
            }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}