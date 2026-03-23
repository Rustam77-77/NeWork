package ru.netology.nework.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.ItemJobBinding
import ru.netology.nework.dto.Job
import java.text.SimpleDateFormat
import java.util.Locale

class JobAdapter(
    private val onDeleteClickListener: (Job) -> Unit
) : ListAdapter<Job, JobAdapter.JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JobViewHolder(binding, onDeleteClickListener)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class JobViewHolder(
        private val binding: ItemJobBinding,
        private val onDeleteClickListener: (Job) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        fun bind(job: Job) {
            binding.apply {
                companyName.text = job.company
                position.text = job.position

                val startDateStr = dateFormat.format(job.startDate)
                val endDateStr = job.endDate?.let { dateFormat.format(it) } ?: "настоящее время"
                period.text = "$startDateStr - $endDateStr"

                deleteButton.setOnClickListener { onDeleteClickListener(job) }
            }
        }
    }

    class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean = oldItem == newItem
    }
}