package ru.netology.nework.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nework.data.database.entities.JobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs WHERE userId = :userId ORDER BY startDate DESC")
    fun getJobsForUser(userId: Long): Flow<List<JobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: JobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(jobs: List<JobEntity>)

    @Query("DELETE FROM jobs WHERE id = :jobId")
    suspend fun deleteById(jobId: Long)

    @Query("DELETE FROM jobs WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Long)
}