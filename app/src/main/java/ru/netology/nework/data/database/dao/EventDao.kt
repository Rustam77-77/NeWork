package ru.netology.nework.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nework.data.database.entities.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY eventDate DESC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :eventId")
    fun getEventById(eventId: Long): Flow<EventEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteById(eventId: Long)

    @Query("DELETE FROM events")
    suspend fun deleteAll()
}