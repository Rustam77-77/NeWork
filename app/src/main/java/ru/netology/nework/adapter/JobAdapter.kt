package ru.netology.nework.adapter

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
    private val onDeleteClickListener: (Job) -> Unit = {},
    private val onEditClickListener: (Job) -> Unit = {}
) : ListAdapter<Job, JobAdapter.JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class JobViewHolder(
        private val binding: ItemJobBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(job: Job) {
            binding.apply {
                companyTextView.text = job.name
                positionTextView.text = job.position

                // Форматирование периода работы
                try {
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("ru"))
                    val startDate = dateFormat.format(
                        java.time.LocalDate.parse(job.start).toEpochDay()
                    )

                    val period = if (job.finish != null) {
                        val finishDate = dateFormat.format(
                            java.time.LocalDate.parse(job.finish).toEpochDay()
                        )
                        "$startDate - $finishDate"
                    } else {
                        "$startDate - настоящее время"
                    }

                    periodTextView.text = period
                } catch (e: Exception) {
                    periodTextView.text = "${job.start} - ${job.finish ?: "настоящее время"}"
                }

                // Ссылка
                if (!job.link.isNullOrBlank()) {
                    linkTextView.visibility = ViewGroup.VISIBLE
                    linkTextView.text = job.link
                    linkTextView.setOnClickListener {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(job.link)
                        )
                        root.context.startActivity(intent)
                    }
                } else {
                    linkTextView.visibility = ViewGroup.GONE
                }

                // Кнопки редактирования и удаления
                if (onEditClickListener != JobAdapter({}, {})::onEditClickListener ||
                    onDeleteClickListener != JobAdapter({}, {})::onDeleteClickListener) {
                    editButton.visibility = ViewGroup.VISIBLE
                    deleteButton.visibility = ViewGroup.VISIBLE

                    editButton.setOnClickListener {
                        onEditClickListener(job)
                    }

                    deleteButton.setOnClickListener {
                        onDeleteClickListener(job)
                    }
                } else {
                    editButton.visibility = ViewGroup.GONE
                    deleteButton.visibility = ViewGroup.GONE
                }
            }
        }
    }

    class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem == newItem
        }
    }
}