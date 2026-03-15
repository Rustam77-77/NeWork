package ru.netology.nework.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentCreatePostBinding
import ru.netology.nework.util.AndroidUtils
import ru.netology.nework.viewmodel.PostViewModel
@AndroidEntryPoint
class CreatePostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        val view = binding.root
        val editText = view.findViewById<EditText>(R.id.edit)
        val okButton = view.findViewById<MaterialButton>(R.id.ok)
        editText?.requestFocus()
        okButton?.setOnClickListener {
            val content = editText?.text?.toString() ?: ""
            viewModel.changeContent(content)
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }
        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        return view
    }
}