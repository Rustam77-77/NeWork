package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import ru.netology.nework.R
import ru.netology.nework.databinding.CardEventBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType

class EventAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventViewHolder(
        private val binding: CardEventBinding,
        private val onInteractionListener: OnInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.apply {
                val eventType = event.type ?: EventType.OFFLINE

                when (eventType) {
                    EventType.ONLINE -> type.text = "Онлайн"
                    EventType.OFFLINE -> type.text = "Оффлайн"
                }

                title.text = event.title
                content.text = event.content
                datetime.text = event.datetime
                authorName.text = event.author
                likesCounter.text = event.likeCount?.toString() ?: "0"
                participantsCounter.text = event.participantsCount?.toString() ?: "0"
                speakersCounter.text = event.speakers?.size?.toString() ?: "0"

                // Устанавливаем состояние кнопок через background
                like.setImageResource(
                    if (event.likedByMe == true) R.drawable.ic_like_checked
                    else R.drawable.ic_like
                )

                participants.setImageResource(
                    if (event.participatedByMe == true) R.drawable.ic_participants_checked
                    else R.drawable.ic_participants
                )

                // Загрузка аватара
                if (!event.authorAvatar.isNullOrBlank()) {
                    authorAvatar.load(event.authorAvatar) {
                        placeholder(R.drawable.ic_default_avatar)
                        error(R.drawable.ic_default_avatar)
                        transformations(CircleCropTransformation())
                    }
                } else {
                    authorAvatar.setImageResource(R.drawable.ic_default_avatar)
                }

                like.setOnClickListener {
                    onInteractionListener.onLikeClicked(event)
                }

                participants.setOnClickListener {
                    onInteractionListener.onParticipateClicked(event)
                }

                speakers.setOnClickListener {
                    onInteractionListener.onSpeakersClicked(event)
                }

                root.setOnClickListener {
                    onInteractionListener.onEventClicked(event)
                }

                root.setOnLongClickListener {
                    if (event.ownedByMe == true) {
                        onInteractionListener.onRemoveClicked(event)
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }

    interface OnInteractionListener {
        fun onLikeClicked(event: Event)
        fun onDislikeClicked(event: Event)
        fun onParticipateClicked(event: Event)
        fun onLeaveClicked(event: Event)
        fun onSpeakersClicked(event: Event)
        fun onEventClicked(event: Event)
        fun onRemoveClicked(event: Event)
    }

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean =
            oldItem == newItem
    }
}