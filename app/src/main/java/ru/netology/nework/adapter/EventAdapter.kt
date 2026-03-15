package ru.netology.nework.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.databinding.CardEventBinding
import ru.netology.nework.dto.Event
class EventAdapter : ListAdapter<Event, EventViewHolder>(EventDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder =
        EventViewHolder(CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
class EventViewHolder(private val binding: CardEventBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(event: Event) {
        // Безопасное заполнение данных через findViewById
        binding.root.findViewById<TextView>(R.id.author)?.text = event.author
        binding.root.findViewById<TextView>(R.id.published)?.text = event.published
        binding.root.findViewById<TextView>(R.id.content)?.text = event.content

        // Отображение количества лайков
        binding.root.findViewById<TextView>(R.id.like)?.text = event.likeOwnerIds.size.toString()
    }
}
class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem == newItem
}