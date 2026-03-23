package ru.netology.nework.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.data.database.dao.JobDao
import ru.netology.nework.data.database.entities.toEntity
import ru.netology.nework.data.database.entities.toModel
import ru.netology.nework.dto.Job
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobRepository @Inject constructor(
    private val jobDao: JobDao,
    private val apiService: ApiService
) {
    fun getJobsForUser(userId: Long): Flow<List<Job>> =
        jobDao.getJobsForUser(userId).map { entities -> entities.map { it.toModel() } }

    suspend fun refreshJobsForUser(userId: Long) {
        try {
            val response = apiService.getJobsForUser(userId)
            if (response.isSuccessful) {
                response.body()?.let { jobs ->
                    jobDao.insertAll(jobs.map { it.toEntity() })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun createJob(job: Job): Job? {
        return try {
            val response = apiService.createJob(job)
            if (response.isSuccessful) {
                response.body()?.also { createdJob ->
                    jobDao.insert(createdJob.toEntity())
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteJob(jobId: Long): Boolean {
        return try {
            val response = apiService.deleteJob(jobId)
            if (response.isSuccessful) {
                jobDao.deleteById(jobId)
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