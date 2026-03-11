package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentUserWallBinding
import ru.netology.nework.viewmodel.UserWallViewModel

@AndroidEntryPoint
class UserWallFragment : Fragment() {

    private var _binding: FragmentUserWallBinding? = null
    private val binding get() = _binding!!

    private val userWallViewModel: UserWallViewModel by viewModels()

    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: Long): UserWallFragment {
            val fragment = UserWallFragment()
            val args = Bundle()
            args.putLong(ARG_USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserWallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        userWallViewModel.posts.observe(viewLifecycleOwner) { posts ->
            // TODO: Set adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}