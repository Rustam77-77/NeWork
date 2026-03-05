package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.ItemEventBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.utils.DateFormatter

class EventAdapter(
    private val onLikeClickListener: (Event) -> Unit,
    private val onMenuClickListener: (Event) -> Unit,
    private val onItemClickListener: (Event) -> Unit
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EventViewHolder(
        private val binding: ItemEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.apply {
                // Аватар
                if (!event.authorAvatar.isNullOrBlank()) {
                    avatarImageView.load(event.authorAvatar) {
                        crossfade(true)
                        placeholder(R.drawable.ic_avatar_placeholder)
                        error(R.drawable.ic_avatar_placeholder)
                    }
                } else {
                    avatarImageView.setImageResource(R.drawable.ic_avatar_placeholder)
                }

                // Имя автора
                authorNameTextView.text = event.author

                // Дата публикации
                publishedTextView.text = DateFormatter.formatDate(event.published)

                // Дата проведения
                datetimeTextView.text = DateFormatter.formatEventDate(event.datetime)

                // Тип события с иконкой
                when (event.type) {
                    EventType.OFFLINE -> {
                        typeIconImageView.setImageResource(R.drawable.ic_location)
                        typeTextView.text = "Офлайн"
                    }
                    EventType.ONLINE -> {
                        typeIconImageView.setImageResource(R.drawable.ic_online)
                        typeTextView.text = "Онлайн"
                    }
                }

                // Текст
                contentTextView.text = event.content

                // Лайки
                likesCountTextView.text = event.likes.toString()
                likeImageView.setImageResource(
                    if (event.likedByMe) R.drawable.ic_like_filled
                    else R.drawable.ic_like_outline
                )

                // Обработчики
                likeImageView.setOnClickListener {
                    animateLike()
                    onLikeClickListener(event)
                }

                menuImageView.setOnClickListener {
                    onMenuClickListener(event)
                }

                root.setOnClickListener {
                    onItemClickListener(event)
                }

                // Вложение
                if (event.attachment != null) {
                    attachmentContainer.visibility = View.VISIBLE
                    when (event.attachment.type) {
                        ru.netology.nework.dto.AttachmentType.IMAGE -> {
                            attachmentTypeIcon.setImageResource(R.drawable.ic_image)
                            attachmentImageView.load(event.attachment.url) {
                                crossfade(true)
                                placeholder(R.drawable.ic_image_placeholder)
                            }
                        }
                        ru.netology.nework.dto.AttachmentType.VIDEO -> {
                            attachmentTypeIcon.setImageResource(R.drawable.ic_video)
                            attachmentImageView.setImageResource(R.drawable.video_placeholder)
                        }
                        ru.netology.nework.dto.AttachmentType.AUDIO -> {
                            attachmentTypeIcon.setImageResource(R.drawable.ic_audio)
                            attachmentImageView.setImageResource(R.drawable.audio_placeholder)
                        }
                    }
                } else {
                    attachmentContainer.visibility = View.GONE
                }

                // Ссылка
                if (!event.link.isNullOrBlank()) {
                    linkContainer.visibility = View.VISIBLE
                    linkTextView.text = event.link
                    linkTextView.setOnClickListener {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(event.link)
                        )
                        root.context.startActivity(intent)
                    }
                } else {
                    linkContainer.visibility = View.GONE
                }
            }
        }

        private fun animateLike() {
            val scaleAnimation = ScaleAnimation(
                1f, 1.3f, 1f, 1.3f,
                binding.likeImageView.width / 2f,
                binding.likeImageView.height / 2f
            )
            scaleAnimation.duration = 150
            binding.likeImageView.startAnimation(scaleAnimation)

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

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}