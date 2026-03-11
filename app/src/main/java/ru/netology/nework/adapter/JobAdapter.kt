package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
<<<<<<< HEAD
import ru.netology.nework.R
=======
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
import ru.netology.nework.databinding.ItemJobBinding
import ru.netology.nework.dto.Job
import java.text.SimpleDateFormat
import java.util.Locale

class JobAdapter(
<<<<<<< HEAD
    private val onEditClick: (Job) -> Unit = {},
    private val onDeleteClick: (Job) -> Unit = {}
=======
    private val onDeleteClickListener: (Job) -> Unit = {},
    private val onEditClickListener: (Job) -> Unit = {}
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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
<<<<<<< HEAD
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
=======
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
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                }

                // Ссылка
                if (!job.link.isNullOrBlank()) {
<<<<<<< HEAD
                    tvLink.visibility = ViewGroup.VISIBLE
                    tvLink.text = job.link
                    tvLink.setOnClickListener {
=======
                    linkTextView.visibility = ViewGroup.VISIBLE
                    linkTextView.text = job.link
                    linkTextView.setOnClickListener {
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(job.link)
                        )
                        root.context.startActivity(intent)
                    }
                } else {
<<<<<<< HEAD
                    tvLink.visibility = ViewGroup.GONE
                }

                // Кнопки редактирования и удаления
                btnEdit.setOnClickListener { onEditClick(job) }
                btnDelete.setOnClickListener { onDeleteClick(job) }
=======
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
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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