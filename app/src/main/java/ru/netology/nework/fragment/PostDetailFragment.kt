package ru.netology.nework.ui.fragment

<<<<<<< HEAD
=======
import android.os.Build
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
import android.widget.Toast
=======
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
import androidx.fragment.app.Fragment
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentPostDetailBinding
import ru.netology.nework.dto.Post
<<<<<<< HEAD
import ru.netology.nework.dto.AttachmentType
=======
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
import java.text.SimpleDateFormat
import java.util.Locale

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

<<<<<<< HEAD
        val post = arguments?.getSerializable("post") as? Post
=======
        // ИСПРАВЛЕНО: безопасное получение Serializable с учетом версий API
        val post = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("post", Post::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("post") as? Post
        }

>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
        post?.let { displayPost(it) }
    }

    private fun displayPost(post: Post) {
        binding.apply {
<<<<<<< HEAD
            // Аватар
=======
            // Загрузка аватара
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            if (!post.authorAvatar.isNullOrBlank()) {
                ivAvatar.load(post.authorAvatar) {
                    crossfade(true)
                    placeholder(R.drawable.ic_avatar_placeholder)
                    error(R.drawable.ic_avatar_placeholder)
                }
            } else {
                ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
            }

            // Имя автора
<<<<<<< HEAD
            tvAuthorName.text = post.author ?: "Неизвестный автор"

            // Дата публикации
            tvPublished.text = try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                dateFormat.format(java.time.Instant.parse(post.published).toEpochMilli())
            } catch (e: Exception) {
                post.published ?: "Дата неизвестна"
            }

            // Текст поста
            tvContent.text = post.content ?: ""
=======
            tvAuthorName.text = post.author

            // Дата публикации
            try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                tvPublished.text = dateFormat.format(
                    java.time.Instant.parse(post.published).toEpochMilli()
                )
            } catch (e: Exception) {
                tvPublished.text = post.published
            }

            // Текст поста
            tvContent.text = post.content
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399

            // Место работы автора
            if (!post.authorJob.isNullOrBlank()) {
                tvJob.text = post.authorJob
                tvJob.visibility = View.VISIBLE
            } else {
                tvJob.text = "В поиске работы"
                tvJob.visibility = View.VISIBLE
            }

            // Упоминания
<<<<<<< HEAD
            if (!post.mentionUsers.isNullOrEmpty()) {
                tvMentions.text = "Упоминания: ${post.mentionUsers.joinToString { it.name ?: "" }}"
=======
            if (post.mentionUsers.isNotEmpty()) {
                tvMentions.text = "Упоминания: ${post.mentionUsers.joinToString { it.name }}"
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                tvMentions.visibility = View.VISIBLE
            } else {
                tvMentions.visibility = View.GONE
            }

            // Вложение
            if (post.attachment != null) {
                ivAttachment.visibility = View.VISIBLE
                when (post.attachment.type) {
<<<<<<< HEAD
                    AttachmentType.IMAGE -> {
=======
                    ru.netology.nework.dto.AttachmentType.IMAGE -> {
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                        ivAttachment.load(post.attachment.url) {
                            crossfade(true)
                            placeholder(R.drawable.ic_image_placeholder)
                        }
                    }
<<<<<<< HEAD
                    AttachmentType.VIDEO -> {
                        ivAttachment.setImageResource(R.drawable.ic_video)
                    }
                    AttachmentType.AUDIO -> {
                        ivAttachment.setImageResource(R.drawable.ic_audio)
                    }
                    null -> ivAttachment.visibility = View.GONE
=======
                    ru.netology.nework.dto.AttachmentType.VIDEO -> {
                        ivAttachment.setImageResource(R.drawable.ic_video)
                    }
                    ru.netology.nework.dto.AttachmentType.AUDIO -> {
                        ivAttachment.setImageResource(R.drawable.ic_audio)
                    }
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                }
            } else {
                ivAttachment.visibility = View.GONE
            }

            // Ссылка
            if (!post.link.isNullOrBlank()) {
                tvLink.visibility = View.VISIBLE
                tvLink.text = post.link
                tvLink.setOnClickListener {
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(post.link)
                    )
                    requireContext().startActivity(intent)
                }
            } else {
                tvLink.visibility = View.GONE
            }

<<<<<<< HEAD
            // Координаты
=======
            // Координаты (карта)
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            if (post.coords != null) {
                tvMap.visibility = View.VISIBLE
                tvMap.text = "📍 ${post.coords.lat}, ${post.coords.long}"
                tvMap.setOnClickListener {
<<<<<<< HEAD
                    Toast.makeText(requireContext(), "Открыть карту", Toast.LENGTH_SHORT).show()
=======
                    val uri = "geo:${post.coords.lat},${post.coords.long}?q=${post.coords.lat},${post.coords.long}"
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(uri)
                    )
                    startActivity(intent)
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
                }
            } else {
                tvMap.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}