package ru.netology.nework.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.ItemEventBinding
import ru.netology.nework.dto.Event
import java.text.SimpleDateFormat
import java.util.Locale

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

        private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        fun bind(event: Event) {
            binding.apply {
                authorName.text = event.author
                date.text = "Опубликовано: ${dateFormat.format(event.published)}"
                eventDate.text = "Дата проведения: ${dateFormat.format(event.datetime)}"
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
                    linkContainer.visibility = ViewGroup.GONE
                } else {
                    linkContainer.visibility = ViewGroup.VISIBLE
                    linkText.text = event.link
                }

                val isAuthor = event.ownedByMe
                menuButton.visibility = if (isAuthor) ViewGroup.VISIBLE else ViewGroup.GONE

                root.setOnClickListener { onItemClickListener(event) }
                likeButton.setOnClickListener { onLikeClickListener(event) }
                menuButton.setOnClickListener { onMenuClickListener(event, isAuthor) }

                linkContainer.setOnClickListener {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(event.link))
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