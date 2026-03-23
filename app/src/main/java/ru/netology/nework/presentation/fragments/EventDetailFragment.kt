package ru.netology.nework.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentEventDetailBinding
import ru.netology.nework.presentation.viewmodels.EventViewModel
import ru.netology.nework.presentation.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var eventId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            eventId = it.getLong("eventId", 0)
        }

        setupObservers()
        eventViewModel.loadEventById(eventId)
        userViewModel.loadUsers()
    }

    private fun setupObservers() {
        eventViewModel.event.observe(viewLifecycleOwner) { event ->
            event?.let {
                displayEvent(it)
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
    }

    private fun displayEvent(event: ru.netology.nework.dto.Event) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        binding.apply {
            authorName.text = event.authorName
            date.text = "Опубликовано: ${dateFormat.format(event.published)}"
            eventDate.text = "Дата проведения: ${dateFormat.format(event.eventDate)}"
            eventType.text = if (event.type.name == "ONLINE") "Онлайн" else "Офлайн"
            content.text = event.content
            authorJob.text = event.authorJob ?: "В поиске работы"

            // Ссылка
            if (event.link.isNullOrEmpty()) {
                linkContainer.visibility = View.GONE
            } else {
                linkContainer.visibility = View.VISIBLE
                linkText.text = event.link
                linkContainer.setOnClickListener {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(event.link))
                    startActivity(intent)
                }
            }

            // Участники
            if (event.participants.isEmpty()) {
                participantsTitle.visibility = View.GONE
                participantsList.visibility = View.GONE
            } else {
                participantsTitle.visibility = View.VISIBLE
                participantsList.visibility = View.VISIBLE
                loadParticipants(event.participants)
            }

            // Спикеры
            if (event.speakers.isEmpty()) {
                speakersTitle.visibility = View.GONE
                speakersList.visibility = View.GONE
            } else {
                speakersTitle.visibility = View.VISIBLE
                speakersList.visibility = View.VISIBLE
                loadSpeakers(event.speakers)
            }
        }
    }

    private fun loadParticipants(userIds: List<Long>) {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            val participants = users.filter { it.id in userIds }
            val names = participants.joinToString(", ") { it.name }
            binding.participantsList.text = names
        }
    }

    private fun loadSpeakers(userIds: List<Long>) {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            val speakers = users.filter { it.id in userIds }
            val names = speakers.joinToString(", ") { it.name }
            binding.speakersList.text = names
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}