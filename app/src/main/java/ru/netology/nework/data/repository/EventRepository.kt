package ru.netology.nework.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.data.database.dao.EventDao
import ru.netology.nework.data.database.entities.toEntity
import ru.netology.nework.data.database.entities.toModel
import ru.netology.nework.dto.Event
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDao: EventDao,
    private val apiService: ApiService
) {
    fun getAllEvents(): Flow<List<Event>> =
        eventDao.getAllEvents().map { entities -> entities.map { it.toModel() } }

    fun getEventById(eventId: Long): Flow<Event?> =
        eventDao.getEventById(eventId).map { entity -> entity?.toModel() }

    suspend fun refreshEvents() {
        try {
            val response = apiService.getAllEvents()
            if (response.isSuccessful) {
                response.body()?.let { events ->
                    eventDao.insertAll(events.map { it.toEntity() })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun likeEvent(eventId: Long): Event? {
        return try {
            val response = apiService.likeEvent(eventId)
            if (response.isSuccessful) {
                response.body()?.also { updatedEvent ->
                    eventDao.insert(updatedEvent.toEntity())
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun unlikeEvent(eventId: Long): Event? {
        return try {
            val response = apiService.unlikeEvent(eventId)
            if (response.isSuccessful) {
                response.body()?.also { updatedEvent ->
                    eventDao.insert(updatedEvent.toEntity())
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveEvent(event: Event): Event? {
        return try {
            val response = if (event.id == 0L) {
                apiService.createEvent(event)
            } else {
                apiService.updateEvent(event.id, event)
            }

            if (response.isSuccessful) {
                response.body()?.also { updatedEvent ->
                    eventDao.insert(updatedEvent.toEntity())
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteEvent(eventId: Long): Boolean {
        return try {
            val response = apiService.deleteEvent(eventId)
            if (response.isSuccessful) {
                eventDao.deleteById(eventId)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}