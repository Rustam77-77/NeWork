package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.ItemEventBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import java.text.SimpleDateFormat
import java.util.Locale

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
                // Загрузка аватара
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
                try {
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    publishedTextView.text = dateFormat.format(
                        java.time.Instant.parse(event.published).toEpochMilli()
                    )
                } catch (e: Exception) {
                    publishedTextView.text = event.published
                }

                // Дата проведения - проверка на null
                try {
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    datetimeTextView.text = "📅 ${dateFormat.format(
                        java.time.Instant.parse(event.datetime).toEpochMilli()
                    )}"
                } catch (e: Exception) {
                    datetimeTextView.text = "📅 ${event.datetime}"
                }

                // Тип события - БЕЗОПАСНАЯ проверка на null
                typeTextView.text = if (event.type != null) {
                    if (event.type == EventType.OFFLINE) "📍 Офлайн" else "🌐 Онлайн"
                } else {
                    "🌐 Онлайн" // По умолчанию
                }

                // Текст события
                contentTextView.text = event.content

                // Лайки
                likesCountTextView.text = event.likes.toString()
                likeImageView.setImageResource(
                    if (event.likedByMe) R.drawable.ic_like_filled
                    else R.drawable.ic_like_outline
                )

                // Обработчики кликов
                likeImageView.setOnClickListener {
                    onLikeClickListener(event)
                }

                menuImageView.setOnClickListener {
                    onMenuClickListener(event)
                }

                root.setOnClickListener {
                    onItemClickListener(event)
                }

                // Отображение вложения
                if (event.attachment != null) {
                    attachmentImageView.visibility = ViewGroup.VISIBLE
                    when (event.attachment.type) {
                        ru.netology.nework.dto.AttachmentType.IMAGE -> {
                            attachmentImageView.load(event.attachment.url) {
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
                if (!event.link.isNullOrBlank()) {
                    linkTextView.visibility = ViewGroup.VISIBLE
                    linkTextView.text = event.link
                    linkTextView.setOnClickListener {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(event.link)
                        )
                        root.context.startActivity(intent)
                    }
                } else {
                    linkTextView.visibility = ViewGroup.GONE
                }
            }
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