package ru.netology.nework.ui.fragment

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
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentCreatePostBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.viewmodel.PostViewModel
import ru.netology.nework.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private var selectedImageUri: Uri? = null
    private var selectedAttachmentUri: Uri? = null
    private var attachmentType: String? = null // "image", "video", "audio"
    private var selectedLocation: Pair<Double, Double>? = null
    private var selectedMentions: List<Long> = emptyList()

    private val maxFileSize = 15 * 1024 * 1024 // 15 MB в байтах

    private val getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            if (validateFileSize(it)) {
                selectedImageUri = it
                attachmentType = "image"
                binding.ivImagePreview.load(it) {
                    crossfade(true)
                    placeholder(R.drawable.ic_image_placeholder)
                }
                binding.ivImagePreview.visibility = View.VISIBLE
                binding.tvImageSize.text = getFileSizeString(it)
                binding.tvImageSize.visibility = View.VISIBLE
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
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
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
    }

    private fun setupListeners() {
        binding.btnLocation.setOnClickListener {
            // Переход к фрагменту с картой
            Toast.makeText(requireContext(), "Выбор локации на карте", Toast.LENGTH_SHORT).show()
            // Здесь будет навигация к фрагменту карты
            // findNavController().navigate(R.id.action_createPostFragment_to_mapFragment)
        }

        binding.btnMentions.setOnClickListener {
            // Переход к выбору упомянутых пользователей
            Toast.makeText(requireContext(), "Выбор пользователей для упоминания", Toast.LENGTH_SHORT).show()
            // Здесь будет навигация к фрагменту выбора пользователей
            // findNavController().navigate(R.id.action_createPostFragment_to_mentionsFragment)
        }

        binding.btnImage.setOnClickListener {
            getImageContent.launch("image/*")
        }

        binding.btnVideo.setOnClickListener {
            getVideoContent.launch("video/*")
        }

        binding.btnAudio.setOnClickListener {
            getAudioContent.launch("audio/*")
        }

        binding.btnRemoveImage.setOnClickListener {
            selectedImageUri = null
            binding.ivImagePreview.visibility = View.GONE
            binding.tvImageSize.visibility = View.GONE
        }

        binding.btnRemoveAttachment.setOnClickListener {
            selectedAttachmentUri = null
            attachmentType = null
            binding.attachmentContainer.visibility = View.GONE
        }

        binding.btnRemoveLocation.setOnClickListener {
            selectedLocation = null
            binding.locationContainer.visibility = View.GONE
        }
    }

    private fun setupTextValidation() {
        binding.etContent.addTextChangedListener {
            updateSaveButtonState()
        }
    }

    private fun updateSaveButtonState() {
        // Кнопка сохранения активна, если есть текст
        activity?.invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_post_menu, menu)
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
                savePost()
                true
            }
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun savePost() {
        val content = binding.etContent.text.toString()

        if (content.isBlank()) {
            Toast.makeText(requireContext(), "Введите текст поста", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Создание поста с вложениями, упоминаниями и локацией
        Toast.makeText(requireContext(), "Пост создан", Toast.LENGTH_SHORT).show()
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