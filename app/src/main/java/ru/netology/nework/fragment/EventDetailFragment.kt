package ru.netology.nework.ui.fragment

import android.os.Build
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

        // ИСПРАВЛЕНО: безопасное получение Serializable с учетом версий API
        val event = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("event", Event::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("event") as? Event
        }

        currentEvent = event
        event?.let { displayEvent(it) }

        setupListeners()
    }

    private fun displayEvent(event: Event) {
        binding.apply {
            // Загрузка аватара
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
            tvAuthorName.text = event.author

            // Дата публикации
            try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                tvPublished.text = dateFormat.format(
                    java.time.Instant.parse(event.published).toEpochMilli()
                )
            } catch (e: Exception) {
                tvPublished.text = event.published
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
            try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                tvDatetime.text = "📅 ${dateFormat.format(
                    java.time.Instant.parse(event.datetime).toEpochMilli()
                )}"
            } catch (e: Exception) {
                tvDatetime.text = "📅 ${event.datetime}"
            }

            // Тип события
            tvType.text = if (event.type == ru.netology.nework.dto.EventType.OFFLINE)
                "📍 Офлайн" else "🌐 Онлайн"

            // Текст события
            tvContent.text = event.content

            // Спикеры
            if (event.speakers.isNotEmpty()) {
                tvSpeakersTitle.visibility = View.VISIBLE
                tvSpeakers.visibility = View.VISIBLE
                tvSpeakers.text = event.speakers.joinToString { it.name }
            } else {
                tvSpeakersTitle.visibility = View.GONE
                tvSpeakers.visibility = View.GONE
            }

            // Участники
            if (event.participants.isNotEmpty()) {
                tvParticipantsTitle.visibility = View.VISIBLE
                tvParticipants.visibility = View.VISIBLE
                tvParticipants.text = event.participants.joinToString { it.name }
            } else {
                tvParticipantsTitle.visibility = View.GONE
                tvParticipants.visibility = View.GONE
            }

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
                                val isParticipant = event.participantIds.contains(userId)
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

        // ИСПРАВЛЕНО: использование onBackPressedDispatcher вместо deprecated onBackPressed
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}