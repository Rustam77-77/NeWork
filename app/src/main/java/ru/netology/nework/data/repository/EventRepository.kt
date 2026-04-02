package ru.netology.nework.data.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import java.time.Instant

interface EventRepository {
    fun getAllEvents(): Flow<List<Event>>
    fun getEventById(id: Long): Flow<Event?>
    suspend fun refreshEvents()
    suspend fun likeEvent(id: Long): Event?
    suspend fun unlikeEvent(id: Long): Event?
    suspend fun saveEvent(event: Event): Event?
    suspend fun deleteEvent(id: Long): Boolean
    suspend fun createEvent(
        content: String,
        datetime: Instant,
        type: EventType,
        speakerIds: List<Long>,
        participantsIds: List<Long>,
        authorId: Long,
        author: String
    ): Event?
    suspend fun updateEvent(
        id: Long,
        content: String,
        datetime: Instant,
        type: EventType,
        speakerIds: List<Long>,
        participantsIds: List<Long>
    ): Event?
}