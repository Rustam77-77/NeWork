package ru.netology.nework.presentation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentEventDetailBinding
import ru.netology.nework.presentation.viewmodels.EventViewModel
import ru.netology.nework.presentation.viewmodels.UserViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var eventId: Long = 0

    private val dateFormatter = DateTimeFormatter
        .ofPattern("dd.MM.yyyy HH:mm")
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())

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
            binding.progressBar.isVisible = isLoading
        }

        eventViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                eventViewModel.clearError()
            }
        }
    }

    private fun displayEvent(event: ru.netology.nework.dto.Event) {
        binding.apply {
            authorName.text = event.author
            date.text = "Опубликовано: ${dateFormatter.format(event.published)}"
            eventDate.text = "Дата проведения: ${dateFormatter.format(event.datetime)}"
            eventType.text = if (event.type.name == "ONLINE") "Онлайн" else "Офлайн"
            content.text = event.content
            authorJob.text = event.authorJob ?: "В поиске работы"

            if (event.link.isNullOrEmpty()) {
                linkContainer.isVisible = false
            } else {
                linkContainer.isVisible = true
                linkText.text = event.link
                linkContainer.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                    startActivity(intent)
                }
            }

            if (event.participantsIds.isEmpty()) {
                participantsTitle.isVisible = false
                participantsList.isVisible = false
            } else {
                participantsTitle.isVisible = true
                participantsList.isVisible = true
                loadParticipants(event.participantsIds)
            }

            if (event.speakerIds.isEmpty()) {  // ИСПРАВЛЕНО: было speakersIds
                speakersTitle.isVisible = false
                speakersList.isVisible = false
            } else {
                speakersTitle.isVisible = true
                speakersList.isVisible = true
                loadSpeakers(event.speakerIds)  // ИСПРАВЛЕНО: было speakersIds
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