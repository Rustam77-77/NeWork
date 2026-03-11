package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.ItemPostBinding
<<<<<<< HEAD
import ru.netology.nework.dto.*
=======
import ru.netology.nework.dto.Post
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(
<<<<<<< HEAD
    private val onItemClick: (Post) -> Unit,
    private val onLikeClick: (Post) -> Unit,
    private val onMenuClick: (Post) -> Unit
=======
    private val onLikeClickListener: (Post) -> Unit,
    private val onMenuClickListener: (Post) -> Unit,
    private val onItemClickListener: (Post) -> Unit
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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
<<<<<<< HEAD
                // Аватар
                if (!post.authorAvatar.isNullOrBlank()) {
                    ivAvatar.load(post.authorAvatar) {
=======
                // Загрузка аватара
                if (!post.authorAvatar.isNullOrBlank()) {
                    avatarImageView.load(post.authorAvatar) {
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                        crossfade(true)
                        placeholder(R.drawable.ic_avatar_placeholder)
                        error(R.drawable.ic_avatar_placeholder)
                    }
                } else {
<<<<<<< HEAD
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
=======
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
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                    else R.drawable.ic_like_outline
                )

                // Обработчики кликов
<<<<<<< HEAD
                root.setOnClickListener { onItemClick(post) }
                btnLike.setOnClickListener { onLikeClick(post) }
                btnMenu.setOnClickListener { onMenuClick(post) }

                // Вложение
                if (post.attachment != null) {
                    ivAttachment.visibility = ViewGroup.VISIBLE
                    when (post.attachment.type) {
                        AttachmentType.IMAGE -> {
                            ivAttachment.load(post.attachment.url) {
=======
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
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                                crossfade(true)
                                placeholder(R.drawable.ic_image_placeholder)
                            }
                        }
<<<<<<< HEAD
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
=======
                        ru.netology.nework.dto.AttachmentType.VIDEO -> {
                            attachmentImageView.setImageResource(R.drawable.ic_video)
                        }
                        ru.netology.nework.dto.AttachmentType.AUDIO -> {
                            attachmentImageView.setImageResource(R.drawable.ic_audio)
                        }
                    }
                } else {
                    attachmentImageView.visibility = ViewGroup.GONE
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                }

                // Ссылка
                if (!post.link.isNullOrBlank()) {
<<<<<<< HEAD
                    tvLink.visibility = ViewGroup.VISIBLE
                    tvLink.text = post.link
                    tvLink.setOnClickListener {
=======
                    linkTextView.visibility = ViewGroup.VISIBLE
                    linkTextView.text = post.link
                    linkTextView.setOnClickListener {
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(post.link)
                        )
                        root.context.startActivity(intent)
                    }
                } else {
<<<<<<< HEAD
                    tvLink.visibility = ViewGroup.GONE
=======
                    linkTextView.visibility = ViewGroup.GONE
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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