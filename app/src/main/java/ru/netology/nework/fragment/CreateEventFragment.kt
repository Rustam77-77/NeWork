package ru.netology.nework.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentCreateEventBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

class CreateEventFragment : Fragment() {

    private var _binding: FragmentCreateEventBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels()
    private var selectedDateTime: String = java.time.Instant.now().toString()
    private var selectedDate: Date = Date()

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

        setupDatePicker()
        setupListeners()
        updateDateTimeDisplay()
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.time

                // Здесь можно добавить выбор времени
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                selectedDateTime = dateFormat.format(selectedDate)
                updateDateTimeDisplay()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveEvent()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateDateTimeDisplay() {
        try {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val date = dateFormat.format(Date.from(java.time.Instant.parse(selectedDateTime)))
            binding.tvDateTime.text = "Дата: $date"
        } catch (e: Exception) {
            binding.tvDateTime.text = "Дата: ${selectedDateTime.take(10)}"
        }
    }

    private fun saveEvent() {
        val content = binding.etContent.text.toString()

        if (content.isBlank()) {
            binding.tilContent.error = "Введите описание события"
            return
        }

        // Получаем выбранный тип события
        val selectedType = when (binding.radioGroup.checkedRadioButtonId) {
            R.id.rbOnline -> EventType.ONLINE
            R.id.rbOffline -> EventType.OFFLINE
            else -> EventType.ONLINE
        }

        // Создаем новое событие
        val newEvent = Event(
            id = 0,
            authorId = 0,
            author = "",
            authorAvatar = null,
            authorJob = null,
            content = content,
            datetime = selectedDateTime,
            published = java.time.Instant.now().toString(),
            coords = null,
            type = selectedType,
            link = null,
            speakerIds = emptyList(),
            speakers = emptyList(),
            participantIds = emptyList(),
            participants = emptyList(),
            attachment = null,
            likeOwnerIds = emptyList(),
            likedByMe = false
        )

        eventViewModel.saveEvent(newEvent)
        Toast.makeText(requireContext(), "Событие создано", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}