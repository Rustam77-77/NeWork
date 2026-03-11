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
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentPostsBinding
import ru.netology.nework.adapter.PostAdapter
import ru.netology.nework.dto.Post
import ru.netology.nework.viewmodel.PostViewModel
import ru.netology.nework.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostsFragment : Fragment() {
    // ... остальной код
}