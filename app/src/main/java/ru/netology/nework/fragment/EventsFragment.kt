package ru.netology.nework.fragment
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.adapter.EventAdapter
import ru.netology.nework.viewmodel.EventViewModel
@AndroidEntryPoint
class EventsFragment : Fragment(R.layout.fragment_events) {
    private val viewModel: EventViewModel by viewModels()
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        // 1. Находим RecyclerView через ID напрямую.
        // Убедитесь, что в res/layout/fragment_events.xml ID установлен как android:id="@+id/list"
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)

        // 2. Инициализируем адаптер
        val adapter = EventAdapter()

        // 3. Устанавливаем адаптер в список
        recyclerView?.adapter = adapter
        // 4. Подписываемся на данные из ViewModel
        viewModel.events.observe(viewLifecycleOwner) { events ->
            // Когда данные пришли, отправляем их в адаптер
            adapter.submitList(events)
        }
        // 5. Обработка ошибок (если во ViewModel есть LiveData для ошибок)
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), "Ошибка: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }
        // 6. Загружаем данные с сервера
        viewModel.loadEvents()
    }
}