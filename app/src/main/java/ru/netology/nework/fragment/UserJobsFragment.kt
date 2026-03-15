package ru.netology.nework.fragment
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.adapter.JobAdapter
import ru.netology.nework.adapter.OnJobInteractionListener
import ru.netology.nework.dto.Job
import ru.netology.nework.viewmodel.UserJobsViewModel
@AndroidEntryPoint
class UserJobsFragment : Fragment(R.layout.fragment_user_jobs) {
    private val viewModel: UserJobsViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 1. Ищем RecyclerView через ID
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)

        // 2. Ищем FloatingActionButton или обычную кнопку через ID.
        // Если кнопки в XML нет, addJobFab будет null, и приложение не упадет.
        val addJobFab = view.findViewById<View>(R.id.addJobFab)
        val adapter = JobAdapter(object : OnJobInteractionListener {
            override fun onRemove(job: Job) {
                viewModel.deleteJob(job.id)
            }
            override fun onEdit(job: Job) {}
        })
        recyclerView?.adapter = adapter
        // Получаем userId из аргументов
        val userId = arguments?.getLong("userId") ?: 0L

        // Подписка на данные
        viewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            adapter.submitList(jobs)
        }
        // Загрузка данных
        viewModel.loadUserJobs(userId)

        // 3. Безопасная установка слушателя (сработает только если View найдена)
        addJobFab?.setOnClickListener {
            // Здесь ваша логика навигации к созданию работы
        }
    }
    companion object {
        fun newInstance(userId: Long) = UserJobsFragment().apply {
            arguments = Bundle().apply {
                putLong("userId", userId)
            }
        }
    }
}