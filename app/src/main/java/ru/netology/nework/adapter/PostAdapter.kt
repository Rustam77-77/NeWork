package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.ItemPostBinding
import ru.netology.nework.dto.*
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(
    private val onItemClick: (Post) -> Unit,
    private val onLikeClick: (Post) -> Unit,
    private val onMenuClick: (Post) -> Unit
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
                // Аватар
                if (!post.authorAvatar.isNullOrBlank()) {
                    ivAvatar.load(post.authorAvatar) {
                        crossfade(true)
                        placeholder(R.drawable.ic_avatar_placeholder)
                        error(R.drawable.ic_avatar_placeholder)
                    }
                } else {
                    ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
                }

                // Имя автора
                tvAuthorName.text = post.author ?: "Неизвестный автор"

                // Дата публикации
                tvPublished.text = try {
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    dateFormat.format(java.time.Instant.parse(post.published).toEpochMilli())
                } catch (e: Exception) {
                    post.published ?: "Дата неизвестна"
                }

                // Текст поста
                tvContent.text = post.content ?: ""

                // Лайки
                tvLikesCount.text = post.likes?.toString() ?: "0"
                btnLike.setImageResource(
                    if (post.likedByMe == true) R.drawable.ic_like_filled
                    else R.drawable.ic_like_outline
                )

                // Обработчики кликов
                root.setOnClickListener { onItemClick(post) }
                btnLike.setOnClickListener { onLikeClick(post) }
                btnMenu.setOnClickListener { onMenuClick(post) }

                // Вложение
                if (post.attachment != null) {
                    ivAttachment.visibility = ViewGroup.VISIBLE
                    when (post.attachment.type) {
                        AttachmentType.IMAGE -> {
                            ivAttachment.load(post.attachment.url) {
                                crossfade(true)
                                placeholder(R.drawable.ic_image_placeholder)
                            }
                        }
                        AttachmentType.VIDEO -> {
                            ivAttachment.setImageResource(R.drawable.ic_video)
                        }
                        AttachmentType.AUDIO -> {
                            ivAttachment.setImageResource(R.drawable.ic_audio)
                        }
                        null -> ivAttachment.visibility = ViewGroup.GONE
                    }
                } else {
                    ivAttachment.visibility = ViewGroup.GONE
                }

                // Ссылка
                if (!post.link.isNullOrBlank()) {
                    tvLink.visibility = ViewGroup.VISIBLE
                    tvLink.text = post.link
                    tvLink.setOnClickListener {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(post.link)
                        )
                        root.context.startActivity(intent)
                    }
                } else {
                    tvLink.visibility = ViewGroup.GONE
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