package ru.netology.nework.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentEditEventBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class EditEventFragment : Fragment() {

    private var _binding: FragmentEditEventBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels()
    private var currentEvent: Event? = null

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

        setupListeners()
    }

    private fun loadEventData(event: Event) {
        binding.apply {
            etContent.setText(event.content ?: "")

            when (event.type) {
                EventType.ONLINE -> rbOnline.isChecked = true
                EventType.OFFLINE -> rbOffline.isChecked = true
                null -> rbOnline.isChecked = true
            }

            try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                val date = dateFormat.format(java.time.Instant.parse(event.datetime).toEpochMilli())
                tvDateTime.text = "Дата: $date"
            } catch (e: Exception) {
                tvDateTime.text = "Дата: ${event.datetime ?: "не указана"}"
            }
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveEvent()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnDateTime.setOnClickListener {
            Toast.makeText(requireContext(), "Выбор даты", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveEvent() {
        val content = binding.etContent.text.toString()
        if (content.isBlank()) {
            Toast.makeText(requireContext(), "Введите описание события", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(requireContext(), "Событие сохранено", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}