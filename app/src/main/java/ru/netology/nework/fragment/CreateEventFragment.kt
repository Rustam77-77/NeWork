package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentCreateEventBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.viewmodel.EventViewModel

@AndroidEntryPoint
class CreateEventFragment : Fragment() {

    private var _binding: FragmentCreateEventBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by activityViewModels()

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

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val content = binding.etContent.text.toString()

            if (title.isBlank() || content.isBlank()) {
                return@setOnClickListener
            }

            val event = Event(
                id = 0,
                authorId = 0,
                author = "",
                authorAvatar = null,
                authorJob = null,
                content = content,
                datetime = "",
                published = "",
                coords = null,
                type = EventType.OFFLINE,
                link = null,
                likeCount = 0,
                dislikeCount = 0,
                likedByMe = false,
                dislikedByMe = false,
                ownedByMe = true,
                participantsCount = 0,
                participatedByMe = false,
                attachment = null,
                speakerIds = emptyList(),
                speakers = emptyList(),
                title = title,
                likeOwnerIds = emptyList(),
                mentionIds = emptyList(),
                mentionedMe = false
            )

            findNavController().navigateUp()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}