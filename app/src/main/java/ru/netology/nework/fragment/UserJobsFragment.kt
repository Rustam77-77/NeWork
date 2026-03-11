package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentUserJobsBinding
import ru.netology.nework.viewmodel.UserJobsViewModel
import ru.netology.nework.adapter.JobAdapter
import ru.netology.nework.dto.Job

@AndroidEntryPoint
class UserJobsFragment : Fragment() {

    private var _binding: FragmentUserJobsBinding? = null
    private val binding get() = _binding!!

    private val userJobsViewModel: UserJobsViewModel by viewModels()

    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: Long): UserJobsFragment {
            val fragment = UserJobsFragment()
            val args = Bundle()
            args.putLong(ARG_USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = JobAdapter(object : JobAdapter.OnInteractionListener {
            override fun onEditClicked(job: Job) {
                // TODO: Implement edit
            }

            override fun onDeleteClicked(job: Job) {
                userJobsViewModel.deleteJob(job)
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        val userId = arguments?.getLong(ARG_USER_ID) ?: 0L
        userJobsViewModel.loadJobs(userId)

        userJobsViewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            adapter.submitList(jobs)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}