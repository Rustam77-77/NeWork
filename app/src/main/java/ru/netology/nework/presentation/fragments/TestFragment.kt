package ru.netology.nework.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val textView = TextView(requireContext())
        textView.text = """
            API Test:
            Проверьте логи в Logcat с тегом ApiTest
            Должны быть видны ответы от сервера
        """.trimIndent()
        textView.textSize = 16f
        textView.setPadding(50, 50, 50, 50)
        return textView
    }
}