package ru.netology.nework.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
<<<<<<< HEAD
import androidx.navigation.fragment.findNavController  // ВАЖНО: добавьте этот импорт!
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
=======
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentEventsBinding
import ru.netology.nework.adapter.EventAdapter
import ru.netology.nework.dto.Event
import ru.netology.nework.viewmodel.EventViewModel
import ru.netology.nework.viewmodel.AuthViewModel
<<<<<<< HEAD
=======
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399

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

        setupRecyclerView()
        setupListeners()
        setupObservers()

        eventViewModel.loadEvents()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(
<<<<<<< HEAD
            onItemClick = { event ->
                openEventDetail(event)
            },
            onLikeClick = { event ->
=======
            onLikeClickListener = { event ->
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                authViewModel.isAuthenticated.observe(viewLifecycleOwner) { isAuth ->
                    if (isAuth == true) {
                        eventViewModel.likeEvent(event)
                    } else {
                        showAuthDialog()
                    }
                }
            },
<<<<<<< HEAD
            onMenuClick = { event ->
=======
            onMenuClickListener = { event ->
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                authViewModel.currentUserId.observe(viewLifecycleOwner) { userId ->
                    if (userId == event.authorId) {
                        showEventMenuDialog(event)
                    }
                }
<<<<<<< HEAD
=======
            },
            onItemClickListener = { event ->
                openEventDetail(event)
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            }
        )

        binding.recyclerViewEvents.apply {
            adapter = eventAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            eventViewModel.loadEvents()
        }
    }

    private fun setupListeners() {
        binding.fabAddEvent.setOnClickListener {
            authViewModel.isAuthenticated.observe(viewLifecycleOwner) { isAuth ->
                if (isAuth == true) {
                    openCreateEvent()
                } else {
                    showAuthDialog()
                }
            }
        }
    }

    private fun setupObservers() {
        eventViewModel.events.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
            binding.swipeRefreshLayout.isRefreshing = false

            if (events.isNullOrEmpty()) {
                binding.textViewEmpty.visibility = View.VISIBLE
            } else {
                binding.textViewEmpty.visibility = View.GONE
            }
        }

        eventViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading == true && eventViewModel.events.value.isNullOrEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        eventViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                eventViewModel.clearError()
            }
        }
    }

    private fun showAuthDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Требуется авторизация")
            .setMessage("Для этого действия необходимо войти в приложение")
            .setPositiveButton("Войти") { _, _ ->
                findNavController().navigate(R.id.action_eventsFragment_to_loginFragment)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEventMenuDialog(event: Event) {
        val items = arrayOf("Редактировать", "Удалить")

        android.app.AlertDialog.Builder(requireContext())
            .setItems(items) { _, which ->
                when (which) {
                    0 -> openEditEvent(event)
                    1 -> confirmDeleteEvent(event)
                }
            }
            .show()
    }

    private fun confirmDeleteEvent(event: Event) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Удаление события")
            .setMessage("Вы уверены, что хотите удалить это событие?")
            .setPositiveButton("Удалить") { _, _ ->
                eventViewModel.removeEvent(event)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun openEventDetail(event: Event) {
        val bundle = Bundle().apply {
            putSerializable("event", event)
        }
        findNavController().navigate(R.id.action_eventsFragment_to_eventDetailFragment, bundle)
    }

    private fun openCreateEvent() {
        findNavController().navigate(R.id.action_eventsFragment_to_createEventFragment)
    }

    private fun openEditEvent(event: Event) {
        val bundle = Bundle().apply {
            putSerializable("event", event)
        }
        findNavController().navigate(R.id.action_eventsFragment_to_editEventFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}