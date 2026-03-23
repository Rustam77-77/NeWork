package ru.netology.nework.presentation.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentCreateEventBinding
import ru.netology.nework.dto.EventType
import ru.netology.nework.presentation.adapters.UserSelectionAdapter
import ru.netology.nework.presentation.viewmodels.AuthViewModel
import ru.netology.nework.presentation.viewmodels.EventViewModel
import ru.netology.nework.presentation.viewmodels.UserViewModel
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class CreateEventFragment : Fragment() {

    private var _binding: FragmentCreateEventBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var selectedSpeakers: MutableList<Long> = mutableListOf()
    private var selectedParticipants: MutableList<Long> = mutableListOf()
    private var selectedEventDate: Date = Date()
    private var eventType: EventType = EventType.ONLINE
    private var eventId: Long = 0
    private var isEditMode: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            eventId = it.getLong("eventId", 0)
            isEditMode = eventId != 0L
        }

        setupMenu()
        setupObservers()
        setupListeners()
        updateEventDateDisplay()

        if (isEditMode) {
            eventViewModel.loadEventById(eventId)
        } else {
            userViewModel.loadUsers()
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.save_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        saveEvent()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupObservers() {
        eventViewModel.event.observe(viewLifecycleOwner) { event ->
            event?.let {
                binding.eventContent.setText(it.content)
                selectedEventDate = it.eventDate
                eventType = it.type
                selectedSpeakers.addAll(it.speakers)
                selectedParticipants.addAll(it.participants)

                updateEventDateDisplay()
                updateSelectedSpeakersCount()
                updateSelectedParticipantsCount()

                if (it.type == EventType.ONLINE) {
                    binding.onlineRadio.isChecked = true
                } else {
                    binding.offlineRadio.isChecked = true
                }
            }
        }

        eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        eventViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                eventViewModel.clearError()
            }
        }

        eventViewModel.isCreated.observe(viewLifecycleOwner) { isCreated ->
            if (isCreated) {
                findNavController().popBackStack()
                eventViewModel.clearCreated()
            }
        }

        userViewModel.users.observe(viewLifecycleOwner) { users ->
            if (users.isNotEmpty()) {
                // Пользователи загружены
            }
        }
    }

    private fun setupListeners() {
        binding.onlineRadio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                eventType = EventType.ONLINE
            }
        }

        binding.offlineRadio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                eventType = EventType.OFFLINE
            }
        }

        binding.selectDateButton.setOnClickListener {
            showDateTimePicker()
        }

        binding.selectSpeakersButton.setOnClickListener {
            showSpeakersSelectionDialog()
        }

        binding.selectParticipantsButton.setOnClickListener {
            showParticipantsSelectionDialog()
        }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance().apply {
            time = selectedEventDate
        }

        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                selectedEventDate = calendar.time
                updateEventDateDisplay()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateEventDateDisplay() {
        binding.selectedDate.text = android.text.format.DateFormat.format("dd.MM.yyyy HH:mm", selectedEventDate)
    }

    private fun showSpeakersSelectionDialog() {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            val adapter = UserSelectionAdapter(
                users = users,
                selectedUserIds = selectedSpeakers,
                onUserSelected = { userId, isChecked ->
                    if (isChecked) {
                        selectedSpeakers.add(userId)
                    } else {
                        selectedSpeakers.remove(userId)
                    }
                    updateSelectedSpeakersCount()
                }
            )

            val dialogView = layoutInflater.inflate(R.layout.dialog_user_selection, null)
            val recyclerView = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Выберите спикеров")
                .setView(dialogView)
                .setPositiveButton("ОК") { _, _ ->
                    updateSelectedSpeakersCount()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    private fun showParticipantsSelectionDialog() {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            val adapter = UserSelectionAdapter(
                users = users,
                selectedUserIds = selectedParticipants,
                onUserSelected = { userId, isChecked ->
                    if (isChecked) {
                        selectedParticipants.add(userId)
                    } else {
                        selectedParticipants.remove(userId)
                    }
                    updateSelectedParticipantsCount()
                }
            )

            val dialogView = layoutInflater.inflate(R.layout.dialog_user_selection, null)
            val recyclerView = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Выберите участников")
                .setView(dialogView)
                .setPositiveButton("ОК") { _, _ ->
                    updateSelectedParticipantsCount()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    private fun updateSelectedSpeakersCount() {
        val count = selectedSpeakers.size
        binding.selectedSpeakersCount.text = if (count > 0) {
            "Выбрано спикеров: $count"
        } else {
            "Спикеры не выбраны"
        }
    }

    private fun updateSelectedParticipantsCount() {
        val count = selectedParticipants.size
        binding.selectedParticipantsCount.text = if (count > 0) {
            "Выбрано участников: $count"
        } else {
            "Участники не выбраны"
        }
    }

    private fun saveEvent() {
        val content = binding.eventContent.text.toString()
        if (content.isBlank()) {
            Snackbar.make(binding.root, "Введите описание события", Snackbar.LENGTH_LONG).show()
            return
        }

        val currentUserId = authViewModel.currentUserId.value
        val currentUserName = "Пользователь"

        if (currentUserId == null) {
            Snackbar.make(binding.root, "Необходимо авторизоваться", Snackbar.LENGTH_LONG).show()
            return
        }

        if (isEditMode) {
            eventViewModel.updateEvent(
                eventId = eventId,
                content = content,
                eventDate = selectedEventDate,
                type = eventType,
                speakers = selectedSpeakers,
                participants = selectedParticipants
            )
        } else {
            eventViewModel.createEvent(
                content = content,
                eventDate = selectedEventDate,
                type = eventType,
                speakers = selectedSpeakers,
                participants = selectedParticipants,
                authorId = currentUserId,
                authorName = currentUserName
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}