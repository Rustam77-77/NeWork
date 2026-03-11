package ru.netology.nework.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import coil.load
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentPostDetailBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.AttachmentType
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

        val post = arguments?.getSerializable("post") as? Post
        post?.let { displayPost(it) }
    }

    private fun displayPost(post: Post) {
        binding.apply {
            // Аватар
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

            // Место работы автора
            if (!post.authorJob.isNullOrBlank()) {
                tvJob.text = post.authorJob
                tvJob.visibility = View.VISIBLE
            } else {
                tvJob.text = "В поиске работы"
                tvJob.visibility = View.VISIBLE
            }

            // Упоминания
            if (!post.mentionUsers.isNullOrEmpty()) {
                tvMentions.text = "Упоминания: ${post.mentionUsers.joinToString { it.name ?: "" }}"
                tvMentions.visibility = View.VISIBLE
            } else {
                tvMentions.visibility = View.GONE
            }

            // Вложение
            if (post.attachment != null) {
                ivAttachment.visibility = View.VISIBLE
                when (post.attachment.type) {
                    AttachmentType.IMAGE -> {
                        ivAttachment.load(post.attachment.url) {
                            crossfade(true)
                            placeholder(R.drawable.ic_image_placeholder)
                        }
                    }
                    AttachmentType.VIDEO -> {
                        ivAttachment.setImageResource(R.drawable.ic_video)
                    }
                    AttachmentType.AUDIO -> {
                        ivAttachment.setImageResource(R.drawable.ic_audio)
                    }
                    null -> ivAttachment.visibility = View.GONE
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

            // Координаты
            if (post.coords != null) {
                tvMap.visibility = View.VISIBLE
                tvMap.text = "📍 ${post.coords.lat}, ${post.coords.long}"
                tvMap.setOnClickListener {
                    Toast.makeText(requireContext(), "Открыть карту", Toast.LENGTH_SHORT).show()
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