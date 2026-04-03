package ru.netology.nework.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.ItemJobBinding
import ru.netology.nework.dto.Job
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class JobAdapter(
    private val onJobClickListener: (Job) -> Unit
) : ListAdapter<Job, JobAdapter.JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JobViewHolder(binding, onJobClickListener)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class JobViewHolder(
        private val binding: ItemJobBinding,
        private val onJobClickListener: (Job) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormatter = DateTimeFormatter
            .ofPattern("dd.MM.yyyy")
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault())

        fun bind(job: Job) {
            binding.apply {
                jobName.text = job.name  // company -> name
                positionName.text = job.position

                val startDate = dateFormatter.format(job.start)
                startDateText.text = startDate

                val endDate = job.finish?.let { dateFormatter.format(it) } ?: "по настоящее время"
                endDateText.text = endDate

                root.setOnClickListener { onJobClickListener(job) }
            }
        }
    }

    class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean = oldItem == newItem
    }
}