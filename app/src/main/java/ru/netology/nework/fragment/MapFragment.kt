package ru.netology.nework.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nework.databinding.FragmentMapBinding

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var selectedLat: Double? = null
    private var selectedLng: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap()
        setupListeners()
    }

    private fun setupMap() {
        // Здесь будет инициализация карты
        // Например, Google Maps или Yandex Maps
        binding.tvMapPlaceholder.text = "Здесь будет карта"
    }

    private fun setupListeners() {
        binding.btnConfirm.setOnClickListener {
            if (selectedLat != null && selectedLng != null) {
                // Возвращаем координаты в предыдущий фрагмент
                val bundle = Bundle().apply {
                    putDouble("lat", selectedLat!!)
                    putDouble("lng", selectedLng!!)
                }
                // В реальном приложении здесь будет navigation pop с результатом
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Выберите точку на карте", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}