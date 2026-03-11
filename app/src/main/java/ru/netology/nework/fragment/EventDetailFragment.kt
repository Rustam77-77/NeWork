package ru.netology.nework.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentEventDetailBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private var currentEvent: Event? = null

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

        val event = arguments?.getSerializable("event") as? Event
        currentEvent = event
        event?.let { displayEvent(it) }

        setupListeners()
    }

    private fun displayEvent(event: Event) {
        binding.apply {
            // Аватар
            if (!event.authorAvatar.isNullOrBlank()) {
                ivAvatar.load(event.authorAvatar) {
                    crossfade(true)
                    placeholder(R.drawable.ic_avatar_placeholder)
                    error(R.drawable.ic_avatar_placeholder)
                }
            } else {
                ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
            }

            // Имя автора
            tvAuthorName.text = event.author ?: "Неизвестный автор"

            // Дата публикации
            tvPublished.text = try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                dateFormat.format(java.time.Instant.parse(event.published).toEpochMilli())
            } catch (e: Exception) {
                event.published ?: "Дата неизвестна"
            }

            // Место работы автора
            if (!event.authorJob.isNullOrBlank()) {
                tvJob.text = event.authorJob
                tvJob.visibility = View.VISIBLE
            } else {
                tvJob.text = "В поиске работы"
                tvJob.visibility = View.VISIBLE
            }

            // Дата проведения
            tvDatetime.text = try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                "📅 ${dateFormat.format(java.time.Instant.parse(event.datetime).toEpochMilli())}"
            } catch (e: Exception) {
                "📅 ${event.datetime ?: "Дата неизвестна"}"
            }

            // Тип события
            tvType.text = when (event.type) {
                ru.netology.nework.dto.EventType.OFFLINE -> "📍 Офлайн"
                ru.netology.nework.dto.EventType.ONLINE -> "🌐 Онлайн"
                null -> "❓ Неизвестный тип"
            }

            // Текст события
            tvContent.text = event.content ?: ""

            // Спикеры
            if (!event.speakers.isNullOrEmpty()) {
                tvSpeakersTitle.visibility = View.VISIBLE
                tvSpeakers.visibility = View.VISIBLE
                tvSpeakers.text = event.speakers.joinToString { it.name ?: "Неизвестно" }
            } else {
                tvSpeakersTitle.visibility = View.GONE
                tvSpeakers.visibility = View.GONE
            }

            // Участники
            if (!event.participants.isNullOrEmpty()) {
                tvParticipantsTitle.visibility = View.VISIBLE
                tvParticipants.visibility = View.VISIBLE
                tvParticipants.text = event.participants.joinToString { it.name ?: "Неизвестно" }
            } else {
                tvParticipantsTitle.visibility = View.GONE
                tvParticipants.visibility = View.GONE
            }

            // Кнопка участия
            authViewModel.currentUserId.observe(viewLifecycleOwner) { userId ->
                if (userId != null && !event.participantIds.isNullOrEmpty()) {
                    val isParticipant = event.participantIds.contains(userId)
                    btnParticipate.text = if (isParticipant) "Отменить участие" else "Принять участие"
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnParticipate.setOnClickListener {
            authViewModel.isAuthenticated.observe(viewLifecycleOwner) { isAuth ->
                if (isAuth == true) {
                    currentEvent?.let { event ->
                        authViewModel.currentUserId.observe(viewLifecycleOwner) { userId ->
                            if (userId != null) {
                                val isParticipant = event.participantIds?.contains(userId) ?: false
                                if (isParticipant) {
                                    eventViewModel.unregisterFromEvent(event)
                                } else {
                                    eventViewModel.registerForEvent(event)
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Требуется авторизация", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}