package ru.netology.nework.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.CardJobBinding
import ru.netology.nework.dto.Job
// ИНТЕРФЕЙС ДОЛЖЕН БЫТЬ ТУТ
interface OnJobInteractionListener {
    fun onRemove(job: Job)
    fun onEdit(job: Job)
}
class JobAdapter(
    private val onInteractionListener: OnJobInteractionListener
) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding, onInteractionListener)
    }
    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
class JobViewHolder(
    private val binding: CardJobBinding,
    private val onInteractionListener: OnJobInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(job: Job) {
        binding.apply {
            // Тут привязка данных (компания, годы)
            // remove.setOnClickListener { onInteractionListener.onRemove(job) }
        }
    }
}
class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean = oldItem == newItem
}