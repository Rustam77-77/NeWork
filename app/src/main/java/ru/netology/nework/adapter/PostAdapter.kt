package ru.netology.nework.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.ItemPostBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.utils.DateFormatter

class PostAdapter(
    private val onLikeClickListener: (Post) -> Unit,
    private val onMenuClickListener: (Post) -> Unit,
    private val onItemClickListener: (Post) -> Unit
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    private var isLoading = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        if (position == itemCount - 1 && !isLoading) {
            // Здесь можно добавить пагинацию
        }
        holder.bind(getItem(position))
    }

    inner class PostViewHolder(
        private val binding: ItemPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                // Анимация загрузки аватара
                avatarImageView.alpha = 0f
                avatarImageView.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()

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
                publishedTextView.text = DateFormatter.formatDate(post.published)

                // Текст поста
                contentTextView.text = post.content

                // Лайки с анимацией
                likesCountTextView.text = post.likes.toString()

                // Обработчик лайка с анимацией
                likeImageView.setOnClickListener {
                    animateLike()
                    onLikeClickListener(post)
                }

                // Устанавливаем иконку лайка
                likeImageView.setImageResource(
                    if (post.likedByMe) R.drawable.ic_like_filled
                    else R.drawable.ic_like_outline
                )

                // Обработчик меню
                menuImageView.setOnClickListener {
                    onMenuClickListener(post)
                }

                // Обработчик клика на карточку
                root.setOnClickListener {
                    onItemClickListener(post)
                }

                // Отображение вложения с иконкой типа
                if (post.attachment != null) {
                    attachmentContainer.visibility = View.VISIBLE

                    // Устанавливаем иконку типа вложения
                    when (post.attachment.type) {
                        AttachmentType.IMAGE -> {
                            attachmentTypeIcon.setImageResource(R.drawable.ic_image)
                            attachmentImageView.load(post.attachment.url) {
                                crossfade(true)
                                placeholder(R.drawable.ic_image_placeholder)
                            }
                        }
                        AttachmentType.VIDEO -> {
                            attachmentTypeIcon.setImageResource(R.drawable.ic_video)
                            attachmentImageView.setImageResource(R.drawable.video_placeholder)
                        }
                        AttachmentType.AUDIO -> {
                            attachmentTypeIcon.setImageResource(R.drawable.ic_audio)
                            attachmentImageView.setImageResource(R.drawable.audio_placeholder)
                        }
                    }

                    // Анимация появления вложения
                    attachmentContainer.alpha = 0f
                    attachmentContainer.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()
                } else {
                    attachmentContainer.visibility = View.GONE
                }

                // Ссылка
                if (!post.link.isNullOrBlank()) {
                    linkContainer.visibility = View.VISIBLE
                    linkTextView.text = post.link
                    linkTextView.setOnClickListener {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(post.link)
                        )
                        root.context.startActivity(intent)
                    }
                } else {
                    linkContainer.visibility = View.GONE
                }
            }
        }

        private fun animateLike() {
            // Анимация лайка
            val scaleAnimation = ScaleAnimation(
                1f, 1.3f, 1f, 1.3f,
                binding.likeImageView.width / 2f,
                binding.likeImageView.height / 2f
            )
            scaleAnimation.duration = 150

            binding.likeImageView.startAnimation(scaleAnimation)

            // Анимация счетчика
            binding.likesCountTextView.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(150)
                .withEndAction {
                    binding.likesCountTextView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150)
                        .start()
                }.start()
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