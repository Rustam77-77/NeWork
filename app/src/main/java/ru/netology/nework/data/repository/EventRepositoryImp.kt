package ru.netology.nework.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.data.database.dao.EventDao
import ru.netology.nework.data.database.entities.toEntity
import ru.netology.nework.data.database.entities.toModel
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val apiService: ApiService
) : EventRepository {

    override fun getAllEvents(): Flow<List<Event>> {
        return eventDao.getAll().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override fun getEventById(id: Long): Flow<Event?> {
        return eventDao.getById(id).map { entity ->
            entity?.toModel()
        }
    }

    override suspend fun refreshEvents() {
        try {
            val response = apiService.getAllEvents()
            if (response.isSuccessful) {
                response.body()?.let { events ->
                    eventDao.insertAll(events.map { it.toEntity() })
                }
            }
        } catch (e: Exception) {
            // Обработка ошибки
        }
    }

    override suspend fun likeEvent(id: Long): Event? {
        return try {
            val response = apiService.likeEvent(id)
            if (response.isSuccessful) {
                response.body()?.also { event ->
                    eventDao.insert(event.toEntity())
                }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun unlikeEvent(id: Long): Event? {
        return try {
            val response = apiService.unlikeEvent(id)
            if (response.isSuccessful) {
                response.body()?.also { event ->
                    eventDao.insert(event.toEntity())
                }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveEvent(event: Event): Event? {
        return try {
            val response = if (event.id == 0L) {
                apiService.createEvent(event)
            } else {
                apiService.updateEvent(event.id, event)
            }
            if (response.isSuccessful) {
                response.body()?.also { newEvent ->
                    eventDao.insert(newEvent.toEntity())
                }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteEvent(id: Long): Boolean {
        return try {
            val response = apiService.deleteEvent(id)
            if (response.isSuccessful) {
                eventDao.deleteById(id)
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun createEvent(
        content: String,
        datetime: Instant,
        type: EventType,
        speakerIds: List<Long>,
        participantsIds: List<Long>,
        authorId: Long,
        author: String
    ): Event? {
        val event = Event(
            id = 0,
            authorId = authorId,
            author = author,
            content = content,
            published = Instant.now(),
            datetime = datetime,
            type = type,
            speakerIds = speakerIds,
            participantsIds = participantsIds,
            likeOwnerIds = emptyList(),
            likedByMe = false,
            ownedByMe = true
        )
        return saveEvent(event)
    }

    override suspend fun updateEvent(
        id: Long,
        content: String,
        datetime: Instant,
        type: EventType,
        speakerIds: List<Long>,
        participantsIds: List<Long>
    ): Event? {
        val flow = getEventById(id)
        val existingEvent = flow.firstOrNull() ?: return null

        val updatedEvent = existingEvent.copy(
            content = content,
            datetime = datetime,
            type = type,
            speakerIds = speakerIds,
            participantsIds = participantsIds
        )
        return saveEvent(updatedEvent)
    }
}