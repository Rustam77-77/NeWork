package ru.netology.nework.data.repository

import kotlinx.coroutines.flow.firstOrNull
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

    suspend fun getJobsForUser(userId: Long): List<Job> {
        return try {
            val response = apiService.getJobsForUser(userId)
            if (response.isSuccessful) {
                response.body()?.let { jobs ->
                    jobDao.insertAll(jobs.map { it.toEntity() })
                    jobs
                } ?: emptyList()
            } else {
                val jobsFlow = jobDao.getByUserId(userId)
                jobsFlow.firstOrNull()?.map { it.toModel() } ?: emptyList()
            }
        } catch (e: Exception) {
            val jobsFlow = jobDao.getByUserId(userId)
            jobsFlow.firstOrNull()?.map { it.toModel() } ?: emptyList()
        }
    }

    suspend fun createJob(job: Job): Job? {
        return try {
            val response = apiService.createJob(job)
            if (response.isSuccessful) {
                response.body()?.also { newJob ->
                    jobDao.insert(newJob.toEntity())
                }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateJob(job: Job): Job? {
        return try {
            val response = apiService.updateJob(job.id, job)
            if (response.isSuccessful) {
                response.body()?.also { updatedJob ->
                    jobDao.insert(updatedJob.toEntity())
                }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteJob(id: Long): Boolean {
        return try {
            val response = apiService.deleteJob(id)
            if (response.isSuccessful) {
                jobDao.deleteById(id)
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun refreshJobs(userId: Long) {
        try {
            val response = apiService.getJobsForUser(userId)
            if (response.isSuccessful) {
                response.body()?.let { jobs ->
                    jobDao.insertAll(jobs.map { it.toEntity() })
                }
            }
        } catch (e: Exception) {
            // Обработка ошибки
        }
    }
}