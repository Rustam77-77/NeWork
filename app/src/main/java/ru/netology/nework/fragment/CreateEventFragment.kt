package ru.netology.nework.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
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
import ru.netology.nework.databinding.FragmentCreateEventBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.viewmodel.EventViewModel
import ru.netology.nework.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.*

@AndroidEntryPoint
class CreateEventFragment : Fragment() {

    private var _binding: FragmentCreateEventBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private var selectedImageUri: Uri? = null
    private var selectedAttachmentUri: Uri? = null
    private var attachmentType: String? = null // "image", "video", "audio"
    private var selectedLocation: Pair<Double, Double>? = null
    private var selectedSpeakers: List<Long> = emptyList()
    private var eventType: EventType = EventType.ONLINE
    private var selectedDateTime: Date? = null

    private val maxFileSize = 15 * 1024 * 1024 // 15 MB в байтах

    private val calendar = Calendar.getInstance()

    private val getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            if (validateFileSize(it)) {
                selectedImageUri = it
                attachmentType = "image"
                binding.ivImagePreview.load(it) {
                    crossfade(true)
                    placeholder(R.drawable.ic_image_placeholder)
                }
                binding.imagePreviewContainer.visibility = View.VISIBLE
                binding.tvImageSize.text = getFileSizeString(it)
            } else {
                Toast.makeText(requireContext(), "Файл слишком большой. Максимальный размер 15 МБ", Toast.LENGTH_LONG).show()
            }
        }
    }

    private val getVideoContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            if (validateFileSize(it)) {
                selectedAttachmentUri = it
                attachmentType = "video"
                binding.ivAttachmentIcon.setImageResource(R.drawable.ic_video)
                binding.tvAttachmentName.text = getFileName(it)
                binding.tvAttachmentSize.text = getFileSizeString(it)
                binding.attachmentContainer.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "Файл слишком большой. Максимальный размер 15 МБ", Toast.LENGTH_LONG).show()
            }
        }
    }

    private val getAudioContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            if (validateFileSize(it)) {
                selectedAttachmentUri = it
                attachmentType = "audio"
                binding.ivAttachmentIcon.setImageResource(R.drawable.ic_audio)
                binding.tvAttachmentName.text = getFileName(it)
                binding.tvAttachmentSize.text = getFileSizeString(it)
                binding.attachmentContainer.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "Файл слишком большой. Максимальный размер 15 МБ", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateEventBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Проверяем, авторизован ли пользователь
        authViewModel.isAuthenticated.observe(viewLifecycleOwner) { isAuth ->
            if (isAuth != true) {
                Toast.makeText(requireContext(), "Необходимо авторизоваться", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        setupListeners()
        setupTextValidation()
        setDefaultDateTime()
    }

    private fun setDefaultDateTime() {
        // Устанавливаем дату по умолчанию (текущая + 1 день)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 18)
        calendar.set(Calendar.MINUTE, 0)
        selectedDateTime = calendar.time
        updateDateTimeDisplay()
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

        // Выбор локации
        binding.btnLocation.setOnClickListener {
            Toast.makeText(requireContext(), "Выбор локации на карте", Toast.LENGTH_SHORT).show()
            // Здесь будет навигация к фрагменту карты
            // findNavController().navigate(R.id.action_createEventFragment_to_mapFragment)
        }

        // Выбор спикеров
        binding.btnSpeakers.setOnClickListener {
            Toast.makeText(requireContext(), "Выбор спикеров", Toast.LENGTH_SHORT).show()
            // Здесь будет навигация к фрагменту выбора пользователей
            // findNavController().navigate(R.id.action_createEventFragment_to_speakersFragment)
        }

        // Выбор изображения
        binding.btnImage.setOnClickListener {
            getImageContent.launch("image/*")
        }

        // Выбор видео
        binding.btnVideo.setOnClickListener {
            getVideoContent.launch("video/*")
        }

        // Выбор аудио
        binding.btnAudio.setOnClickListener {
            getAudioContent.launch("audio/*")
        }

        // Удаление изображения
        binding.btnRemoveImage.setOnClickListener {
            selectedImageUri = null
            binding.imagePreviewContainer.visibility = View.GONE
        }

        // Удаление вложения
        binding.btnRemoveAttachment.setOnClickListener {
            selectedAttachmentUri = null
            attachmentType = null
            binding.attachmentContainer.visibility = View.GONE
        }

        // Удаление локации
        binding.btnRemoveLocation.setOnClickListener {
            selectedLocation = null
            binding.locationContainer.visibility = View.GONE
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
            val dateFormat = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
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
        inflater.inflate(R.menu.create_event_menu, menu)
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
                saveEvent()
                true
            }
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveEvent() {
        val content = binding.etContent.text.toString()

        if (content.isBlank()) {
            Toast.makeText(requireContext(), "Введите описание события", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDateTime == null) {
            Toast.makeText(requireContext(), "Выберите дату проведения", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Создание события с вложениями, спикерами и локацией
        Toast.makeText(requireContext(), "Событие создано", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun validateFileSize(uri: Uri): Boolean {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val fileSize = inputStream?.available() ?: 0
            inputStream?.close()
            fileSize <= maxFileSize
        } catch (e: Exception) {
            false
        }
    }

    private fun getFileSizeString(uri: Uri): String {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val fileSize = inputStream?.available() ?: 0
            inputStream?.close()

            when {
                fileSize < 1024 -> "$fileSize B"
                fileSize < 1024 * 1024 -> "${fileSize / 1024} KB"
                else -> "${fileSize / (1024 * 1024)} MB"
            }
        } catch (e: Exception) {
            ""
        }
    }

    private fun getFileName(uri: Uri): String {
        return try {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                it.moveToFirst()
                return it.getString(nameIndex)
            } ?: uri.path?.substringAfterLast("/") ?: "file"
        } catch (e: Exception) {
            "file"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}