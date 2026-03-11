package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.CardJobBinding
import ru.netology.nework.dto.Job
import java.text.SimpleDateFormat
import java.util.Locale

class JobAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Job, JobAdapter.JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class JobViewHolder(
        private val binding: CardJobBinding,
        private val onInteractionListener: OnInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        fun bind(job: Job) {
            binding.apply {
                name.text = job.name
                position.text = job.position

                val startDate = formatDate(job.start)
                val endDate = job.finish?.let { formatDate(it) } ?: "настоящее время"
                period.text = "$startDate - $endDate"

                if (!job.link.isNullOrBlank()) {
                    link.text = job.link
                    link.visibility = android.view.View.VISIBLE
                } else {
                    link.visibility = android.view.View.GONE
                }

                if (job.ownedByMe) {
                    editButton.visibility = android.view.View.VISIBLE
                    deleteButton.visibility = android.view.View.VISIBLE

                    editButton.setOnClickListener {
                        onInteractionListener.onEditClicked(job)
                    }

                    deleteButton.setOnClickListener {
                        onInteractionListener.onDeleteClicked(job)
                    }
                } else {
                    editButton.visibility = android.view.View.GONE
                    deleteButton.visibility = android.view.View.GONE
                }
            }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val date = dateFormat.parse(dateString)
                SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(date ?: return dateString)
            } catch (e: Exception) {
                dateString
            }
        }
    }

    interface OnInteractionListener {
        fun onEditClicked(job: Job)
        fun onDeleteClicked(job: Job)
    }

    class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean =
            oldItem == newItem
    }
}