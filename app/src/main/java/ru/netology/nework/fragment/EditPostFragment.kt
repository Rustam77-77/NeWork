package ru.netology.nework.ui.fragment

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
import ru.netology.nework.databinding.FragmentEditPostBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.viewmodel.PostViewModel
import ru.netology.nework.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private var currentPost: Post? = null
    private var selectedImageUri: Uri? = null

    private val maxFileSize = 15 * 1024 * 1024 // 15 MB в байтах

    private val getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            if (validateFileSize(it)) {
                selectedImageUri = it
                binding.ivImagePreview.load(it) {
                    crossfade(true)
                    placeholder(R.drawable.ic_image_placeholder)
                    error(R.drawable.ic_image_placeholder)
                }
                binding.imagePreviewContainer.visibility = View.VISIBLE
                binding.tvImageSize.text = getFileSizeString(it)
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
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем пост из аргументов
        currentPost = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("post", Post::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("post") as? Post
        }

        // Заполняем поля данными поста
        currentPost?.let { post ->
            binding.etContent.setText(post.content)

            // Если есть вложение, показываем его
            if (post.attachment != null && post.attachment.type == ru.netology.nework.dto.AttachmentType.IMAGE) {
                binding.ivImagePreview.load(post.attachment.url) {
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
        binding.btnImage.setOnClickListener {
            getImageContent.launch("image/*")
        }

        binding.btnRemoveImage.setOnClickListener {
            selectedImageUri = null
            binding.imagePreviewContainer.visibility = View.GONE
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
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
        inflater.inflate(R.menu.edit_post_menu, menu)
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
                updatePost()
                true
            }
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updatePost() {
        val content = binding.etContent.text.toString()

        if (content.isBlank()) {
            Toast.makeText(requireContext(), "Введите текст поста", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Обновление поста
        Toast.makeText(requireContext(), "Пост обновлен", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}