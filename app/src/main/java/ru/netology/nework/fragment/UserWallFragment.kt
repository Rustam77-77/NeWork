package ru.netology.nework.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
<<<<<<< HEAD
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentUserWallBinding
import ru.netology.nework.adapter.PostAdapter
=======
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.adapter.PostAdapter
import ru.netology.nework.databinding.FragmentUserWallBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.viewmodel.AuthViewModel
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
import ru.netology.nework.viewmodel.UserWallViewModel

@AndroidEntryPoint
class UserWallFragment : Fragment() {

    private var _binding: FragmentUserWallBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserWallViewModel by viewModels()
<<<<<<< HEAD
    private lateinit var postAdapter: PostAdapter
=======
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter
    private var userId: Long = 0
    private var currentUserId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getLong("userId") ?: 0
    }
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399

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

<<<<<<< HEAD
        setupRecyclerView()
        setupObservers()

        val userId = (parentFragment as? UserDetailFragment)?.arguments?.getSerializable("user")?.let {
            (it as ru.netology.nework.dto.User).id
        } ?: 0
        viewModel.loadUserWall(userId)
=======
        // Получаем ID текущего пользователя
        authViewModel.currentUserId.observe(viewLifecycleOwner) { id ->
            currentUserId = id
        }

        setupRecyclerView()
        setupObservers()

        if (userId != 0L) {
            viewModel.loadUserWall(userId)
        }
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
<<<<<<< HEAD
            onItemClick = { post ->
                Toast.makeText(requireContext(), "Пост: ${post.id}", Toast.LENGTH_SHORT).show()
            },
            onLikeClick = { post ->
                Toast.makeText(requireContext(), "Лайк: ${post.id}", Toast.LENGTH_SHORT).show()
            },
            onMenuClick = { post ->
                Toast.makeText(requireContext(), "Меню: ${post.id}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerViewWall.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
=======
            onLikeClickListener = { post ->
                // Проверяем авторизацию перед лайком
                authViewModel.isAuthenticated.observe(viewLifecycleOwner) { isAuth ->
                    if (isAuth == true) {
                        viewModel.likePost(post.id, !post.likedByMe)
                    } else {
                        Toast.makeText(requireContext(), "Требуется авторизация", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onMenuClickListener = { post ->
                // Показываем меню только для своих постов
                if (currentUserId == post.authorId) {
                    showPostMenuDialog(post)
                }
            },
            onItemClickListener = { post ->
                // Переход к детальному просмотру поста
                val bundle = Bundle().apply {
                    putSerializable("post", post)
                }
                findNavController().navigate(R.id.action_userWallFragment_to_postDetailFragment, bundle)
            }
        )

        binding.rvWall.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadUserWall(userId)
        }
    }

    private fun showPostMenuDialog(post: Post) {
        val items = arrayOf("Редактировать", "Удалить")

        android.app.AlertDialog.Builder(requireContext())
            .setItems(items) { _, which ->
                when (which) {
                    0 -> {
                        // Переход к редактированию
                        val bundle = Bundle().apply {
                            putSerializable("post", post)
                        }
                        findNavController().navigate(R.id.action_userWallFragment_to_editPostFragment, bundle)
                    }
                    1 -> {
                        // Подтверждение удаления
                        confirmDeletePost(post)
                    }
                }
            }
            .show()
    }

    private fun confirmDeletePost(post: Post) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Удаление поста")
            .setMessage("Вы уверены, что хотите удалить этот пост?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deletePost(post.id)
            }
            .setNegativeButton("Отмена", null)
            .show()
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
    }

    private fun setupObservers() {
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.submitList(posts)
<<<<<<< HEAD
            binding.textViewEmpty.visibility = if (posts.isNullOrEmpty()) View.VISIBLE else View.GONE
=======
            binding.swipeRefreshLayout.isRefreshing = false

            if (posts.isNullOrEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.tvEmpty.visibility = View.GONE
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading == true && viewModel.posts.value.isNullOrEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

<<<<<<< HEAD
=======
    companion object {
        fun newInstance(userId: Long): UserWallFragment {
            val fragment = UserWallFragment()
            val args = Bundle()
            args.putLong("userId", userId)
            fragment.arguments = args
            return fragment
        }
    }

>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}