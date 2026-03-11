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
<<<<<<< HEAD
=======
import ru.netology.nework.dto.EventType
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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
<<<<<<< HEAD
            // Аватар
=======
            // Загрузка аватара
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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
<<<<<<< HEAD
            tvAuthorName.text = event.author ?: "Неизвестный автор"

            // Дата публикации
            tvPublished.text = try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                dateFormat.format(java.time.Instant.parse(event.published).toEpochMilli())
            } catch (e: Exception) {
                event.published ?: "Дата неизвестна"
=======
            tvAuthorName.text = event.author

            // Дата публикации
            try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                tvPublished.text = dateFormat.format(
                    java.time.Instant.parse(event.published).toEpochMilli()
                )
            } catch (e: Exception) {
                tvPublished.text = event.published
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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
<<<<<<< HEAD
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
=======
            try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                tvDatetime.text = "📅 ${dateFormat.format(
                    java.time.Instant.parse(event.datetime).toEpochMilli()
                )}"
            } catch (e: Exception) {
                tvDatetime.text = "📅 ${event.datetime}"
            }

            // Тип события - безопасная обработка
            val eventType = event.type ?: EventType.ONLINE
            tvType.text = when (eventType) {
                EventType.OFFLINE -> "📍 Офлайн"
                EventType.ONLINE -> "🌐 Онлайн"
            }

            // Текст события
            tvContent.text = event.content

            // Спикеры
            if (event.speakers.isNotEmpty()) {
                tvSpeakersTitle.visibility = View.VISIBLE
                tvSpeakers.visibility = View.VISIBLE
                tvSpeakers.text = event.speakers.joinToString { it.name }
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            } else {
                tvSpeakersTitle.visibility = View.GONE
                tvSpeakers.visibility = View.GONE
            }

            // Участники
<<<<<<< HEAD
            if (!event.participants.isNullOrEmpty()) {
                tvParticipantsTitle.visibility = View.VISIBLE
                tvParticipants.visibility = View.VISIBLE
                tvParticipants.text = event.participants.joinToString { it.name ?: "Неизвестно" }
=======
            if (event.participants.isNotEmpty()) {
                tvParticipantsTitle.visibility = View.VISIBLE
                tvParticipants.visibility = View.VISIBLE
                tvParticipants.text = event.participants.joinToString { it.name }
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            } else {
                tvParticipantsTitle.visibility = View.GONE
                tvParticipants.visibility = View.GONE
            }

<<<<<<< HEAD
            // Кнопка участия
            authViewModel.currentUserId.observe(viewLifecycleOwner) { userId ->
                if (userId != null && !event.participantIds.isNullOrEmpty()) {
=======
            // Вложение
            if (event.attachment != null) {
                ivAttachment.visibility = View.VISIBLE
                when (event.attachment.type) {
                    ru.netology.nework.dto.AttachmentType.IMAGE -> {
                        ivAttachment.load(event.attachment.url) {
                            crossfade(true)
                            placeholder(R.drawable.ic_image_placeholder)
                        }
                    }
                    ru.netology.nework.dto.AttachmentType.VIDEO -> {
                        ivAttachment.setImageResource(R.drawable.ic_video)
                    }
                    ru.netology.nework.dto.AttachmentType.AUDIO -> {
                        ivAttachment.setImageResource(R.drawable.ic_audio)
                    }
                }
            } else {
                ivAttachment.visibility = View.GONE
            }

            // Ссылка
            if (!event.link.isNullOrBlank()) {
                tvLink.visibility = View.VISIBLE
                tvLink.text = event.link
                tvLink.setOnClickListener {
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(event.link)
                    )
                    requireContext().startActivity(intent)
                }
            } else {
                tvLink.visibility = View.GONE
            }

            // Координаты (карта)
            if (event.coords != null) {
                tvMap.visibility = View.VISIBLE
                tvMap.text = "📍 ${event.coords.lat}, ${event.coords.long}"
                tvMap.setOnClickListener {
                    val uri = "geo:${event.coords.lat},${event.coords.long}?q=${event.coords.lat},${event.coords.long}"
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(uri)
                    )
                    startActivity(intent)
                }
            } else {
                tvMap.visibility = View.GONE
            }

            // Кнопка участия
            authViewModel.currentUserId.observe(viewLifecycleOwner) { userId ->
                if (userId != null) {
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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
<<<<<<< HEAD
                                val isParticipant = event.participantIds?.contains(userId) ?: false
                                if (isParticipant) {
                                    eventViewModel.unregisterFromEvent(event)
                                } else {
=======
                                val isParticipant = event.participantIds.contains(userId)
                                if (isParticipant) {
                                    // Передаем event, а не id
                                    eventViewModel.unregisterFromEvent(event)
                                } else {
                                    // Передаем event, а не id
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
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