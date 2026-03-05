package ru.netology.nework.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentEditEventBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.viewmodel.EventViewModel
import ru.netology.nework.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditEventFragment : Fragment() {

    private var _binding: FragmentEditEventBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private var currentEvent: Event? = null
    private var selectedImageUri: Uri? = null
    private var selectedDateTime: Date? = null
    private var eventType: EventType = EventType.ONLINE

    private val calendar = Calendar.getInstance()

    private val getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.ivImagePreview.load(it) {
                crossfade(true)
                placeholder(R.drawable.ic_image_placeholder)
                error(R.drawable.ic_image_placeholder)
            }
            binding.imagePreviewContainer.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditEventBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем событие из аргументов
        currentEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("event", Event::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("event") as? Event
        }

        // Заполняем поля данными события
        currentEvent?.let { event ->
            binding.etContent.setText(event.content)

            // Устанавливаем тип события
            eventType = event.type
            when (event.type) {
                EventType.ONLINE -> binding.rbOnline.isChecked = true
                EventType.OFFLINE -> binding.rbOffline.isChecked = true
            }

            // Устанавливаем дату
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                selectedDateTime = inputFormat.parse(event.datetime)

                val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                binding.tvDateTime.text = outputFormat.format(selectedDateTime ?: Date())
                binding.tvDateTime.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Если есть изображение, показываем его
            if (event.attachment != null && event.attachment.type == ru.netology.nework.dto.AttachmentType.IMAGE) {
                binding.ivImagePreview.load(event.attachment.url) {
                    crossfade(true)
                    placeholder(R.drawable.ic_image_placeholder)
                    error(R.drawable.ic_image_placeholder)
                }
                binding.imagePreviewContainer.visibility = View.VISIBLE
            }
        }

        setupListeners()
        setupTextValidation()
    }

    private fun setupListeners() {
        // Переключатель типа события
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            eventType = when (checkedId) {
                R.id.rbOnline -> EventType.ONLINE
                R.id.rbOffline -> EventType.OFFLINE
                else -> EventType.ONLINE
            }
        }

        // Выбор даты и времени
        binding.btnDateTime.setOnClickListener {
            showDateTimePicker()
        }

        // Выбор изображения
        binding.btnImage.setOnClickListener {
            getImageContent.launch("image/*")
        }

        // Удаление изображения
        binding.btnRemoveImage.setOnClickListener {
            selectedImageUri = null
            binding.imagePreviewContainer.visibility = View.GONE
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showDateTimePicker() {
        val currentDate = calendar

        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)

            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                selectedDateTime = calendar.time
                updateDateTimeDisplay()
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show()

        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDateTimeDisplay() {
        selectedDateTime?.let { date ->
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            binding.tvDateTime.text = dateFormat.format(date)
            binding.tvDateTime.visibility = View.VISIBLE
        }
    }

    private fun setupTextValidation() {
        binding.etContent.addTextChangedListener {
            updateSaveButtonState()
        }
    }

    private fun updateSaveButtonState() {
        activity?.invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_event_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val saveItem = menu.findItem(R.id.action_save)
        saveItem.isEnabled = binding.etContent.text.toString().isNotBlank()
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                updateEvent()
                true
            }
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateEvent() {
        val content = binding.etContent.text.toString()

        if (content.isBlank()) {
            Toast.makeText(requireContext(), "Введите описание события", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDateTime == null) {
            Toast.makeText(requireContext(), "Выберите дату проведения", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Обновление события
        Toast.makeText(requireContext(), "Событие обновлено", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}