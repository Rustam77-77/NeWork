package ru.netology.nework.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.data.database.entities.JobEntity

@Dao
interface JobDao {

    @Query("SELECT * FROM jobs ORDER BY start DESC")
    fun getAll(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id")
    suspend fun getById(id: Long): JobEntity?

    @Query("SELECT * FROM jobs WHERE userId = :userId ORDER BY start DESC")
    fun getByUserId(userId: Long): Flow<List<JobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: JobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(jobs: List<JobEntity>)

    @Update
    suspend fun update(job: JobEntity)

    @Delete
    suspend fun delete(job: JobEntity)

    @Query("DELETE FROM jobs WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM jobs WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Long)

    @Query("DELETE FROM jobs")
    suspend fun deleteAll()
}