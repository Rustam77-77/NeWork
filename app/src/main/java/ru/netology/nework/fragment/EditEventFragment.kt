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
import ru.netology.nework.databinding.FragmentEditEventBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

class EditEventFragment : Fragment() {

    private var _binding: FragmentEditEventBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels()
    private var currentEvent: Event? = null
    private var selectedDateTime: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentEvent = arguments?.getSerializable("event") as? Event
        currentEvent?.let { loadEventData(it) }

        setupDatePicker()
        setupListeners()
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                selectedDateTime = dateFormat.format(calendar.time)
                updateDateTimeDisplay()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun loadEventData(event: Event) {
        binding.etContent.setText(event.content)
        selectedDateTime = event.datetime

        // Безопасная обработка типа события
        val eventType = event.type ?: EventType.ONLINE

        if (eventType == EventType.ONLINE) {
            binding.rbOnline.isChecked = true
        } else {
            binding.rbOffline.isChecked = true
        }

        updateDateTimeDisplay()
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

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveEvent()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
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

        // ИСПРАВЛЕНИЕ: сохраняем значение в локальную переменную перед использованием
        val eventToUpdate = currentEvent
        if (eventToUpdate != null) {
            // Создаем обновленное событие
            val updatedEvent = eventToUpdate.copy(
                content = content,
                type = selectedType,
                datetime = if (selectedDateTime.isNotEmpty()) selectedDateTime else eventToUpdate.datetime
            )

            eventViewModel.saveEvent(updatedEvent)
            Toast.makeText(requireContext(), "Событие сохранено", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        } else {
            Toast.makeText(requireContext(), "Ошибка: событие не найдено", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}