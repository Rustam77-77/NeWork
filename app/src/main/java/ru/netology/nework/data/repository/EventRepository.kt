package ru.netology.nework.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.data.database.dao.EventDao
import ru.netology.nework.data.database.entities.EventEntity
import ru.netology.nework.dto.Event
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "EventRepository"

@Singleton
class EventRepository @Inject constructor(
    private val eventDao: EventDao,
    private val apiService: ApiService
) {
    fun getAllEvents(): Flow<List<Event>> =
        eventDao.getAllEvents().map { entities ->
            entities.map { entity ->
                Event(
                    id = entity.id,
                    authorId = entity.authorId,
                    author = entity.author,
                    authorAvatar = entity.authorAvatar,
                    content = entity.content,
                    published = entity.published,
                    datetime = entity.datetime,
                    type = entity.type,
                    likedByMe = entity.likedByMe,
                    likeOwnerIds = entity.likeOwnerIds,
                    link = entity.link,
                    participantsIds = entity.participantsIds,
                    speakersIds = entity.speakersIds,
                    ownedByMe = entity.ownedByMe,
                    authorJob = entity.authorJob
                )
            }
        }

    fun getEventById(eventId: Long): Flow<Event?> =
        eventDao.getEventById(eventId).map { entity ->
            entity?.let {
                Event(
                    id = it.id,
                    authorId = it.authorId,
                    author = it.author,
                    authorAvatar = it.authorAvatar,
                    content = it.content,
                    published = it.published,
                    datetime = it.datetime,
                    type = it.type,
                    likedByMe = it.likedByMe,
                    likeOwnerIds = it.likeOwnerIds,
                    link = it.link,
                    participantsIds = it.participantsIds,
                    speakersIds = it.speakersIds,
                    ownedByMe = it.ownedByMe,
                    authorJob = it.authorJob
                )
            }
        }

    suspend fun refreshEvents() {
        try {
            Log.d(TAG, "refreshEvents: starting network request")
            val response = apiService.getAllEvents()
            Log.d(TAG, "refreshEvents: response code = ${response.code()}")

            if (response.isSuccessful) {
                response.body()?.let { events ->
                    Log.d(TAG, "refreshEvents: received ${events.size} events from server")

                    val entities = events.map { event ->
                        EventEntity(
                            id = event.id,
                            authorId = event.authorId,
                            author = event.author,
                            authorAvatar = event.authorAvatar,
                            content = event.content,
                            published = event.published,
                            datetime = event.datetime,
                            type = event.type,
                            likedByMe = event.likedByMe,
                            likeOwnerIds = event.likeOwnerIds,
                            link = event.link,
                            participantsIds = event.participantsIds,
                            speakersIds = event.speakersIds,
                            ownedByMe = event.ownedByMe,
                            authorJob = event.authorJob
                        )
                    }
                    eventDao.insertAll(entities)
                    Log.d(TAG, "refreshEvents: saved ${entities.size} events to DB")
                } ?: run {
                    Log.e(TAG, "refreshEvents: response body is null")
                }
            } else {
                Log.e(TAG, "refreshEvents: error response ${response.code()}, message: ${response.message()}")
                response.errorBody()?.let {
                    Log.e(TAG, "refreshEvents: error body = ${it.string()}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "refreshEvents: exception", e)
        }
    }

    suspend fun likeEvent(eventId: Long): Event? {
        return try {
            Log.d(TAG, "likeEvent: eventId=$eventId")
            val response = apiService.likeEvent(eventId)
            if (response.isSuccessful) {
                response.body()?.also { updatedEvent ->
                    val entity = EventEntity(
                        id = updatedEvent.id,
                        authorId = updatedEvent.authorId,
                        author = updatedEvent.author,
                        authorAvatar = updatedEvent.authorAvatar,
                        content = updatedEvent.content,
                        published = updatedEvent.published,
                        datetime = updatedEvent.datetime,
                        type = updatedEvent.type,
                        likedByMe = updatedEvent.likedByMe,
                        likeOwnerIds = updatedEvent.likeOwnerIds,
                        link = updatedEvent.link,
                        participantsIds = updatedEvent.participantsIds,
                        speakersIds = updatedEvent.speakersIds,
                        ownedByMe = updatedEvent.ownedByMe,
                        authorJob = updatedEvent.authorJob
                    )
                    eventDao.insert(entity)
                }
            } else {
                Log.e(TAG, "likeEvent: error ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "likeEvent: exception", e)
            null
        }
    }

    suspend fun unlikeEvent(eventId: Long): Event? {
        return try {
            Log.d(TAG, "unlikeEvent: eventId=$eventId")
            val response = apiService.unlikeEvent(eventId)
            if (response.isSuccessful) {
                response.body()?.also { updatedEvent ->
                    val entity = EventEntity(
                        id = updatedEvent.id,
                        authorId = updatedEvent.authorId,
                        author = updatedEvent.author,
                        authorAvatar = updatedEvent.authorAvatar,
                        content = updatedEvent.content,
                        published = updatedEvent.published,
                        datetime = updatedEvent.datetime,
                        type = updatedEvent.type,
                        likedByMe = updatedEvent.likedByMe,
                        likeOwnerIds = updatedEvent.likeOwnerIds,
                        link = updatedEvent.link,
                        participantsIds = updatedEvent.participantsIds,
                        speakersIds = updatedEvent.speakersIds,
                        ownedByMe = updatedEvent.ownedByMe,
                        authorJob = updatedEvent.authorJob
                    )
                    eventDao.insert(entity)
                }
            } else {
                Log.e(TAG, "unlikeEvent: error ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "unlikeEvent: exception", e)
            null
        }
    }

    suspend fun saveEvent(event: Event): Event? {
        return try {
            Log.d(TAG, "saveEvent: eventId=${event.id}")
            val response = if (event.id == 0L) {
                apiService.createEvent(event)
            } else {
                apiService.updateEvent(event.id, event)
            }

            if (response.isSuccessful) {
                response.body()?.also { updatedEvent ->
                    val entity = EventEntity(
                        id = updatedEvent.id,
                        authorId = updatedEvent.authorId,
                        author = updatedEvent.author,
                        authorAvatar = updatedEvent.authorAvatar,
                        content = updatedEvent.content,
                        published = updatedEvent.published,
                        datetime = updatedEvent.datetime,
                        type = updatedEvent.type,
                        likedByMe = updatedEvent.likedByMe,
                        likeOwnerIds = updatedEvent.likeOwnerIds,
                        link = updatedEvent.link,
                        participantsIds = updatedEvent.participantsIds,
                        speakersIds = updatedEvent.speakersIds,
                        ownedByMe = updatedEvent.ownedByMe,
                        authorJob = updatedEvent.authorJob
                    )
                    eventDao.insert(entity)
                }
            } else {
                Log.e(TAG, "saveEvent: error ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "saveEvent: exception", e)
            null
        }
    }

    suspend fun deleteEvent(eventId: Long): Boolean {
        return try {
            Log.d(TAG, "deleteEvent: eventId=$eventId")
            val response = apiService.deleteEvent(eventId)
            if (response.isSuccessful) {
                eventDao.deleteById(eventId)
                Log.d(TAG, "deleteEvent: success")
                true
            } else {
                Log.e(TAG, "deleteEvent: error ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "deleteEvent: exception", e)
            false
        }
    }
}