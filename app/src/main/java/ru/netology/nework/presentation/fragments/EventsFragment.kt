package ru.netology.nework.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentEventsBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.presentation.adapters.EventAdapter
import ru.netology.nework.presentation.viewmodels.AuthViewModel
import ru.netology.nework.presentation.viewmodels.EventViewModel

@AndroidEntryPoint
class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setupRecyclerView()
        setupObservers()
        setupListeners()
        setupSwipeRefresh()

        eventViewModel.loadEvents()
    }

    private fun initAdapter() {
        eventAdapter = EventAdapter(
            onItemClickListener = { event -> openEventDetail(event) },
            onLikeClickListener = { event -> eventViewModel.likeEvent(event) },
            onMenuClickListener = { event, isAuthor -> if (isAuthor) showEventMenuDialog(event) },
            currentUserId = authViewModel.currentUserId.value
        )
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }
    }

    private fun setupObservers() {
        eventViewModel.events.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
            binding.emptyState.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
        }

        eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        eventViewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }

        eventViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                eventViewModel.clearError()
            }
        }

        authViewModel.currentUserId.observe(viewLifecycleOwner) {
            initAdapter()
            binding.recyclerView.adapter = eventAdapter
            eventAdapter.submitList(eventViewModel.events.value)
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            if (authViewModel.isAuthenticated.value == true) {
                findNavController().navigate(R.id.createEventFragment)
            } else {
                showAuthDialog()
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            eventViewModel.refreshEvents()
        }
    }

    private fun openEventDetail(event: Event) {
        val bundle = Bundle().apply {
            putLong("eventId", event.id)
        }
        findNavController().navigate(R.id.eventDetailFragment, bundle)
    }

    private fun showEventMenuDialog(event: Event) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Действия с событием")
            .setItems(arrayOf("Редактировать", "Удалить")) { _, which ->
                when (which) {
                    0 -> {
                        val bundle = Bundle().apply {
                            putLong("eventId", event.id)
                        }
                        findNavController().navigate(R.id.createEventFragment, bundle)
                    }
                    1 -> {
                        showDeleteConfirmationDialog(event.id)
                    }
                }
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(eventId: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удаление события")
            .setMessage("Вы уверены, что хотите удалить это событие?")
            .setPositiveButton("Удалить") { _, _ ->
                eventViewModel.deleteEvent(eventId)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showAuthDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Требуется авторизация")
            .setMessage("Для создания события необходимо войти в аккаунт")
            .setPositiveButton("Войти") { _, _ ->
                findNavController().navigate(R.id.loginFragment)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}