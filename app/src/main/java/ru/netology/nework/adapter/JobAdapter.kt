package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.databinding.ItemJobBinding
import ru.netology.nework.dto.Job
import java.text.SimpleDateFormat
import java.util.Locale

class JobAdapter(
    private val onEditClick: (Job) -> Unit = {},
    private val onDeleteClick: (Job) -> Unit = {}
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
                tvCompanyName.text = job.name
                tvPosition.text = job.position

                // Форматирование периода работы
                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("ru"))

                    val startDate = inputFormat.parse(job.start)
                    val startStr = outputFormat.format(startDate)

                    val period = if (job.finish != null) {
                        val finishDate = inputFormat.parse(job.finish)
                        val finishStr = outputFormat.format(finishDate)
                        "$startStr — $finishStr"
                    } else {
                        "$startStr — настоящее время"
                    }

                    tvPeriod.text = period
                } catch (e: Exception) {
                    tvPeriod.text = "${job.start} — ${job.finish ?: "настоящее время"}"
                }

                // Ссылка
                if (!job.link.isNullOrBlank()) {
                    tvLink.visibility = ViewGroup.VISIBLE
                    tvLink.text = job.link
                    tvLink.setOnClickListener {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(job.link)
                        )
                        root.context.startActivity(intent)
                    }
                } else {
                    tvLink.visibility = ViewGroup.GONE
                }

                // Кнопки редактирования и удаления
                btnEdit.setOnClickListener { onEditClick(job) }
                btnDelete.setOnClickListener { onDeleteClick(job) }
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