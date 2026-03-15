package ru.netology.nework.fragment
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.adapter.EventAdapter
import ru.netology.nework.dto.Event
import ru.netology.nework.viewmodel.UserWallViewModel
@AndroidEntryPoint
class UserWallFragment : Fragment(R.layout.fragment_user_wall) {
    private val viewModel: UserWallViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Поиск RecyclerView через ID напрямую
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        val adapter = EventAdapter()
        recyclerView?.adapter = adapter
        // 2. Получение ID пользователя из аргументов
        val userId = arguments?.getLong("userId") ?: 0L
        // 3. Подписка на данные с маппингом типов (Post -> Event)
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            val eventList = posts.map { post ->
                Event(
                    id = post.id,
                    authorId = post.authorId,
                    author = post.author,
                    authorAvatar = post.authorAvatar,
                    content = post.content,
                    published = post.published,
                    coords = post.coords,
                    link = post.link,
                    // ИСПРАВЛЕНО: Безопасная конвертация List? в Set и обработка null
                    likeOwnerIds = post.likeOwnerIds?.toSet() ?: emptySet(),
                    // ИСПРАВЛЕНО: Преобразование Boolean? в Boolean через Elvis-оператор ?:
                    likedByMe = post.likedByMe ?: false,
                    attachment = post.attachment,
                    // ИСПРАВЛЕНО: Преобразование Boolean? в Boolean
                    ownedByMe = post.ownedByMe ?: false
                )
            }
            // Теперь типы данных полностью совпадают с ожиданиями адаптера
            adapter.submitList(eventList)
        }
        // 4. Загрузка данных со стены пользователя
        viewModel.loadUserWall(userId)
    }
    companion object {
        fun newInstance(userId: Long) = UserWallFragment().apply {
            arguments = Bundle().apply {
                putLong("userId", userId)
            }
        }
    }
}