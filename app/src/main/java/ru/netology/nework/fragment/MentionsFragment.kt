package ru.netology.nework.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nework.databinding.FragmentMentionsBinding
import ru.netology.nework.adapter.UserSelectionAdapter
import ru.netology.nework.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MentionsFragment : Fragment() {

    private var _binding: FragmentMentionsBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var userAdapter: UserSelectionAdapter
    private var selectedUserIds = mutableListOf<Long>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMentionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        userViewModel.loadUsers()
    }

    private fun setupRecyclerView() {
        userAdapter = UserSelectionAdapter(
            onUserSelected = { user, isSelected ->
                if (isSelected) {
                    selectedUserIds.add(user.id)
                } else {
                    selectedUserIds.remove(user.id)
                }
                updateSelectedCount()
            }
        )

        binding.rvUsers.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            userAdapter.submitList(users)
        }
    }

    private fun setupListeners() {
        binding.btnConfirm.setOnClickListener {
            // Возвращаем выбранных пользователей
            val bundle = Bundle().apply {
                putLongArray("selectedUserIds", selectedUserIds.toLongArray())
            }
            // В реальном приложении здесь будет navigation pop с результатом
            Toast.makeText(requireContext(), "Выбрано: ${selectedUserIds.size}", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateSelectedCount() {
        binding.tvSelectedCount.text = "Выбрано: ${selectedUserIds.size}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}