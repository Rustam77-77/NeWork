package ru.netology.nework.presentation.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.ItemEventBinding
import ru.netology.nework.dto.Event
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class EventAdapter(
    private val onItemClickListener: (Event) -> Unit,
    private val onLikeClickListener: (Event) -> Unit,
    private val onMenuClickListener: (Event, Boolean) -> Unit,
    private val currentUserId: Long? = null
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding, onItemClickListener, onLikeClickListener, onMenuClickListener, currentUserId)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventViewHolder(
        private val binding: ItemEventBinding,
        private val onItemClickListener: (Event) -> Unit,
        private val onLikeClickListener: (Event) -> Unit,
        private val onMenuClickListener: (Event, Boolean) -> Unit,
        private val currentUserId: Long?
    ) : RecyclerView.ViewHolder(binding.root) {

        // ИСПРАВЛЕНО: используем DateTimeFormatter вместо SimpleDateFormat
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault())

        fun bind(event: Event) {
            binding.apply {
                authorName.text = event.author

                // ИСПРАВЛЕНО: форматирование Instant без ошибок
                val publishedFormatted = dateTimeFormatter.format(event.published)
                val datetimeFormatted = dateTimeFormatter.format(event.datetime)

                date.text = "Опубликовано: $publishedFormatted"
                eventDate.text = "Дата проведения: $datetimeFormatted"
                eventType.text = if (event.type.name == "ONLINE") "Онлайн" else "Офлайн"
                content.text = event.content
                likeCount.text = event.likeOwnerIds.size.toString()
                likeButton.isChecked = event.likedByMe

                val initials = event.author.split(" ")
                    .take(2)
                    .map { it.firstOrNull() ?: '?' }
                    .joinToString("")
                avatarText.text = initials

                if (event.link.isNullOrEmpty()) {
                    linkContainer.isVisible = false
                } else {
                    linkContainer.isVisible = true
                    linkText.text = event.link
                }

                val isAuthor = event.ownedByMe
                menuButton.isVisible = isAuthor

                root.setOnClickListener { onItemClickListener(event) }
                likeButton.setOnClickListener { onLikeClickListener(event) }
                menuButton.setOnClickListener { onMenuClickListener(event, isAuthor) }

                linkContainer.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                    root.context.startActivity(intent)
                }
            }
        }
    }

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem == newItem
    }
}