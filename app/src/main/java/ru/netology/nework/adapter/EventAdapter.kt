package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.ItemEventBinding
import ru.netology.nework.dto.*
import java.text.SimpleDateFormat
import java.util.Locale

class EventAdapter(
    private val onItemClick: (Event) -> Unit,
    private val onLikeClick: (Event) -> Unit,
    private val onMenuClick: (Event) -> Unit
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
                // Безопасная загрузка аватара
                if (!event.authorAvatar.isNullOrBlank()) {
                    ivAvatar.load(event.authorAvatar) {
                        crossfade(true)
                        placeholder(R.drawable.ic_avatar_placeholder)
                        error(R.drawable.ic_avatar_placeholder)
                    }
                } else {
                    ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
                }

                // Имя автора (с проверкой на null)
                tvAuthorName.text = event.author ?: "Неизвестный автор"

                // Дата публикации (с безопасным парсингом)
                tvPublished.text = try {
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    dateFormat.format(java.time.Instant.parse(event.published).toEpochMilli())
                } catch (e: Exception) {
                    event.published ?: "Дата неизвестна"
                }

                // Дата проведения (с безопасным парсингом)
                tvDatetime.text = try {
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    "📅 ${dateFormat.format(java.time.Instant.parse(event.datetime).toEpochMilli())}"
                } catch (e: Exception) {
                    "📅 ${event.datetime ?: "Дата неизвестна"}"
                }

                // Тип события (с проверкой на null)
                tvType.text = when (event.type) {
                    EventType.OFFLINE -> "📍 Офлайн"
                    EventType.ONLINE -> "🌐 Онлайн"
                    null -> "❓ Неизвестный тип"
                }

                // Текст события (с проверкой на null)
                tvContent.text = event.content ?: ""

                // Количество лайков (с проверкой на null)
                tvLikesCount.text = event.likes?.toString() ?: "0"

                // Иконка лайка (с проверкой на null)
                btnLike.setImageResource(
                    if (event.likedByMe == true) R.drawable.ic_like_filled
                    else R.drawable.ic_like_outline
                )

                // Обработчики кликов
                root.setOnClickListener { onItemClick(event) }
                btnLike.setOnClickListener { onLikeClick(event) }
                btnMenu.setOnClickListener { onMenuClick(event) }

                // Вложение (с безопасной проверкой на null)
                if (event.attachment != null && event.attachment.type != null) {
                    ivAttachment.visibility = ViewGroup.VISIBLE
                    when (event.attachment.type) {
                        AttachmentType.IMAGE -> {
                            ivAttachment.load(event.attachment.url) {
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
                    }
                } else {
                    ivAttachment.visibility = ViewGroup.GONE
                }

                // Ссылка (с проверкой на null)
                if (!event.link.isNullOrBlank()) {
                    tvLink.visibility = ViewGroup.VISIBLE
                    tvLink.text = event.link
                    tvLink.setOnClickListener {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(event.link)
                        )
                        root.context.startActivity(intent)
                    }
                } else {
                    tvLink.visibility = ViewGroup.GONE
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